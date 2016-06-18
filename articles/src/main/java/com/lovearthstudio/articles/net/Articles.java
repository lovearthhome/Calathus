package com.lovearthstudio.articles.net;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lovearthstudio.articles.constant.Constant;
import com.lovearthstudio.duasdk.Dua;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by zhaoliang on 16/4/6.
 */
public class Articles {

    private GetArtParams getArtParams;
    private SetArtParams setArtParams;
    private AddArtParams addArtParams;
    private Context mContext;
    private MyCallBack mMyCallBack;
    private ArtDB artdb;
    private ArtNB artnb;

    public Articles(Context context) {
        mContext = context;

        RealmConfiguration config = new RealmConfiguration.Builder(context).setModules(new ArtModule()).build();
        Realm.setDefaultConfiguration(config);

        artdb = new ArtDB();
        artnb = new ArtNB();
        getArtParams = new GetArtParams();
        setArtParams = new SetArtParams();
        addArtParams = new AddArtParams();
        getArtParams.filter = new HashMap<>();
        setArtParams.filter = new HashMap<>();
    }

    /**
     * 根据tidref获取本地文章
     * 只有三种动作：
     * load: 一开始的加载
     * pull: 获取新的数据
     * push: 获取旧的数据
     * next: 大于tidref的数据
     * 对于评论，我们使用的是仅仅支持网络加载
     */
    public JSONArray getLocalArticle(long tidref/*参考的tid*/) {
        JSONArray tmpdbarticles = new JSONArray();
        try {
            tmpdbarticles = artdb.loadArticle(tidref);
            return tmpdbarticles;
        }catch(Exception e){
            return tmpdbarticles;
        }
    }

    private JSONObject asmArticles(JSONArray dbarticles)
    {
        try {
            JSONObject dbresult = new JSONObject();
            long inc_min = 0;
            long inc_max = 0;
            int artCount = dbarticles.length();
            if (artCount > 0) {
                Log.i("request", "jsonarray has articles:" + artCount);
                JSONObject jsonObject = new JSONObject(dbarticles.get(0).toString());
                long inc0 = jsonObject.optLong("inc");
                int maxi = dbarticles.length() - 1;
                if (maxi < 0) maxi = 0;
                jsonObject = new JSONObject(dbarticles.get(maxi).toString());
                long inc1 = jsonObject.optLong("inc");
                if(inc1 > inc0) {
                    inc_max = inc1;
                    inc_min = inc0;
                } else{
                    inc_max = inc0;
                    inc_min = inc1;
                }
            }


            /**
             *  根据当前指针添加广告到结尾
             * */
            JSONArray adarticles = new JSONArray();
            if(Constant.adflag && Constant.artsSinceLastAd +dbarticles.length()> Constant.ArtsPerAd)
            {
                    for(int i=0;i<dbarticles.length();i++)
                    {
                        adarticles.put(dbarticles.get(i));
                        Constant.artsSinceLastAd++;
                        if(Constant.artsSinceLastAd > Constant.ArtsPerAd)
                        {
                            /**在myArticles里插入ad*/

                            adarticles.put(makeAd());
                            Constant.artsSinceLastAd = 0;
                        }
                    }

            }else{
                adarticles = dbarticles;
            }
            dbresult.put("data", adarticles);
            dbresult.put("inc_min", inc_min);
            dbresult.put("inc_max", inc_max);
            return dbresult;
        }catch (JSONException e){
            Log.e("Error", e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 请求数据
     * 只有三种动作：
     * load: 一开始的加载
     * pull: 获取新的数据
     * push: 获取旧的数据
     * next: 大于tidref的数据
     * 对于评论，我们使用的是仅仅支持网络加载
     */
    public void getArticles(String pchannel, String paction, long ptidref/*参考的tid*/, long pridref, final MyCallBack myCallBack) {
        try {
            /**
             * 预处理参数
             */
            if ("Review".equals(pchannel)) {
                //首先,从本地存储中读出本用户已经审阅过的文章的curReviewTid
                long curReviewTid = 0;
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("articles", Activity.MODE_PRIVATE);
                if (sharedPreferences != null) {
                    curReviewTid = sharedPreferences.getLong("curReviewTid", 0);
                }
                pchannel = "Review";
                paction = "next";
                ptidref = curReviewTid;
            }
            final String channel = pchannel;
            final String action = paction;
            final long tidref = ptidref;
            final long ridref = pridref;


            Log.i("articles.find", channel + "-" + action + " " + tidref);
            //Fixme: 那么我们要从本地加载数据,加载数据只会加载数据库最新的20条数据
            JSONObject dbresult = new JSONObject();
            JSONArray dbarticles;
            if ("load".equals(action)) {
                dbarticles = artdb.loadArticles(channel, ridref, Constant.artdbArticleCountPerFetch);
                myCallBack.onResponse(asmArticles(dbarticles));
                return;
            }

            if ("pull".equals(action)) {
                dbarticles = artdb.pullArticles(channel, ridref, tidref, Constant.artdbArticleCountPerFetch);
                if (dbarticles.length() != 0) {
                    myCallBack.onResponse(asmArticles(dbarticles));
                    return;
                }
            }

            if ("push".equals(action)) {
                dbarticles = artdb.pushArticles(channel, ridref, tidref, Constant.artdbArticleCountPerFetch);
                if (dbarticles.length() != 0) {
                    myCallBack.onResponse(asmArticles(dbarticles));
                    return;
                }
            }

            if ("next".equals(action)) {
                dbarticles = artdb.nextArticles(channel, ridref, tidref, 1);
                if (dbarticles.length() != 0) {
                    myCallBack.onResponse(asmArticles(dbarticles));
                    return;
                }
            }

            mMyCallBack = myCallBack;
            getArtParams.dua_id = Dua.getInstance().getCurrentDuaId();
            getArtParams.action = "get_articles";
            getArtParams.how = action;
            getArtParams.fields = new String[]{"inc", "star", "comt", "content", "good", "bad", "shar"};
            getArtParams.channel = channel;
            getArtParams.filter = new HashMap<>();
            getArtParams.filter.put("inc[>]", 0);

            if (action == "pull") {
                getArtParams.rows = Constant.artdbArticleCountPerFetch;
                getArtParams.filter.clear();
                getArtParams.order = "inc DESC";
                getArtParams.filter.put("inc[>]", tidref);
                getArtParams.filter.put("rid", ridref);
            }

            if (action == "push") {
                getArtParams.rows = Constant.artdbArticleCountPerFetch;
                getArtParams.filter.clear();
                getArtParams.order = "inc DESC";
                getArtParams.filter.put("inc[<]", tidref);
                getArtParams.filter.put("rid", ridref);
            }

            if (action == "next") {
                getArtParams.rows = Constant.artdbArticleCountPerFetch;
                getArtParams.order = "inc ASC";
                getArtParams.filter.clear();
                getArtParams.filter.put("inc[>]", tidref);
                getArtParams.filter.put("rid", ridref);
            }

            final long pmcBeginTime = Calendar.getInstance().getTimeInMillis();

            artnb.getArticles(getArtParams, new MyCallBack() {
                @Override
                public void onFailure(JSONObject result) {
                    //fixme: 这块的onfailure是怎么触发到app主模块里面的？这块应该让app主模块的刷新按钮停止旋转。
                    mMyCallBack.onFailure(result);
                }

                @Override
                public void onResponse(JSONObject jsonResponse) {
                    try {
                        if (jsonResponse == null) {
                            myCallBack.onFailure(Constant.failureObject(Constant.UNKNOWN_FAILURE, "null jsonResponse"));
                            return;
                        }

                        if (jsonResponse.optInt("status") != 0) {
                            //FIXME: 把错误信息发前端显示
                            myCallBack.onFailure(jsonResponse);
                            return;
                        }
                        //FIXME: 这个地方保存加载更新和加载更多的参数
                        JSONObject echo = jsonResponse.optJSONObject("echo");
                        String echo_channel = echo.optString("channel");
                        String echo_action = echo.optString("action");
                        String echo_how = echo.optString("how");
                        long echo_tidref = echo.optLong("tidref");


                        JSONObject result = jsonResponse.optJSONObject("result");
                        JSONArray data = result.getJSONArray("data");
                        int count = data.length();
                        long new_inc_min = result.optLong("inc_min");
                        long new_inc_max = result.optLong("inc_max");
                        int nomore = result.optInt("nomore");

                        //如果服务器返回的数据都是有意义的，不是广告，也有数据
                        if (count > 0) {
                            artdb.storeArticles(channel, ridref, data, new_inc_max, new_inc_min, nomore);
                            JSONArray myArticles;
                            if ("pull".equals(action) || "push".equals(action) || "load".equals(action)) {
                                myArticles = artdb.loadArticles(channel, ridref, new_inc_min, new_inc_max);
                            } else {
                                myArticles = artdb.nextArticles(channel, ridref, tidref, 1);
                            }
                            myCallBack.onResponse(asmArticles(myArticles));
                            long pmcOverTime = Calendar.getInstance().getTimeInMillis();
                            Dua.getInstance().setAppPmc("GetArts", count, "1", pmcOverTime - pmcBeginTime, "ms");
                        } else {
                            myCallBack.onFailure(Constant.failureObject(Constant.NOART_NOTICE, "no articles"));
                        }
                    } catch (JSONException e) {
                        Log.e("Error", e.toString());
                        e.printStackTrace();
                        myCallBack.onFailure(Constant.failureObject(Constant.JSON_FAILURE, e.toString()));
                    }
                }
            });
        } catch (Exception e) {
            Log.e("Error", e.toString());
            e.printStackTrace();
            myCallBack.onFailure(Constant.failureObject(Constant.JSON_FAILURE, e.toString()));
        }
    }

    private  String makeAd() {
        JSONObject adjson = new JSONObject();
        try {
            adjson.put("tmpl",501);
            adjson.put("editor_name","木瓜推广");
            adjson.put("editor_sex","F");
            adjson.put("editor_avatar","http://files.xdua.org/files/avatar/2016042382a703a15270d93ec34b724249450c5e.png");
        } catch (Exception e){}
        return adjson.toString();
    }

    /**
     * 获取审核文章
     * 只有三种动作：
     * load: 一开始的加载
     * pull: 获取新的数据
     * push: 获取旧的数据
     */
    public void setArticle(final long tid, final String from, final String which, final int param, final MyCallBack myCallBack) {
        try {
            setArtParams.tid = tid;
            setArtParams.dua_id = Dua.getInstance().getCurrentDuaId();
            setArtParams.action = "set_article";
            setArtParams.how = from; //"Review"
            setArtParams.field = which; //"good"
            setArtParams.param = param; // 1
            setArtParams.filter.clear();
            setArtParams.filter.put(which, param);

            Log.i("Articles", setArtParams.tid + " " + setArtParams.how);
            final long pmcBeginTime = Calendar.getInstance().getTimeInMillis();
            artnb.setArticle(setArtParams, new MyCallBack() {
                @Override
                public void onFailure(JSONObject failureObjcet) {
                    myCallBack.onFailure(failureObjcet);
                }

                @Override
                public void onResponse(JSONObject jsonResponse) {
                    try {
                        //FIXME: 这儿注意，能进入这个onResponse,那么nb层已经处理好了
                        if (jsonResponse.optInt("status") == 0) {
                            if (from == "Review") {
                                //实例化SharedPreferences对象（第一步）
                                SharedPreferences mySharedPreferences = mContext.getSharedPreferences("articles", Activity.MODE_PRIVATE);
                                //实例化SharedPreferences.Editor对象（第二步）
                                SharedPreferences.Editor editor = mySharedPreferences.edit();
                                //用putString的方法保存数据
                                editor.putLong("curReviewTid", tid);
                                //提交当前数据
                                editor.commit();
                                //使用toast信息提示框提示成功写入数据
                                //Toast.makeText(mContext, "数据成功写入SharedPreferences！" , Toast.LENGTH_LONG).show();
                            }
                            long pmcOverTime = Calendar.getInstance().getTimeInMillis();
                            Dua.getInstance().setAppPmc("SetArts", 1, "1", pmcOverTime - pmcBeginTime, "ms");
                            myCallBack.onResponse(jsonResponse);
                        } else {
                            myCallBack.onFailure(jsonResponse);
                        }
                    } catch (Exception e) {
                        Log.e("Error", e.toString());
                        e.printStackTrace();
                        myCallBack.onFailure(Constant.failureObject(Constant.EXCEPTION, e.toString()));
                    }
                }
            });
        } catch (Exception e) {
            Log.e("Error", e.toString());
            e.printStackTrace();
            myCallBack.onFailure(Constant.failureObject(Constant.EXCEPTION, e.toString()));
        }
    }

    /**
     * 添加文章
     * 只有三种动作：
     * load: 一开始的加载
     * pull: 获取新的数据
     * push: 获取旧的数据
     */
    public void addArticle(long rid, String cato, String media, int flag, int tmpl, String content, final MyCallBack myCallBack) {
        try {
            addArtParams.rid = rid;
            addArtParams.dua_id = Dua.getInstance().getCurrentDuaId();
            addArtParams.action = "add_article";
            addArtParams.from = "com.lovearthstudio.calathus"; //"Review"
            addArtParams.cato = cato; // 1
            addArtParams.media = media; // 1
            addArtParams.flag = flag; // 1
            addArtParams.tmpl = tmpl; // 1
            addArtParams.content = content; // 1

            if(cato == "Comment")
            {
                addArtParams.copycheck = 0;
            }

            Log.i("Articles", addArtParams.rid + " ");
            final long pmcBeginTime = Calendar.getInstance().getTimeInMillis();
            artnb.addArticle(addArtParams, new MyCallBack() {
                @Override
                public void onFailure(JSONObject failureObjcet) {
                    myCallBack.onFailure(failureObjcet);
                }

                @Override
                public void onResponse(JSONObject jsonResponse) {
                    try {
                        //FIXME: 这儿注意，能进入这个onResponse,那么nb层已经处理好了
                        if (jsonResponse.optInt("status") == 0) {
                            long pmcOverTime = Calendar.getInstance().getTimeInMillis();
                            Dua.getInstance().setAppPmc("AddArts", 1, "1", pmcOverTime - pmcBeginTime, "ms");
                            myCallBack.onResponse(jsonResponse);
                        } else {
                            myCallBack.onFailure(jsonResponse);
                        }
                    } catch (Exception e) {
                        Log.e("Error", e.toString());
                        e.printStackTrace();
                        myCallBack.onFailure(Constant.failureObject(Constant.EXCEPTION, e.toString()));
                    }
                }
            });
        } catch (Exception e) {
            Log.e("Error", e.toString());
            e.printStackTrace();
            myCallBack.onFailure(Constant.failureObject(Constant.EXCEPTION, e.toString()));
        }
    }


}
