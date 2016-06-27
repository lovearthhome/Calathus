package com.lovearthstudio.articles.core;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Author：焦帅 on 2016/4/30 16:48
 * Email：jiaoshuaihit@gmail.com
 *
 *  这是一个本地数据库
 */
public class ArtDB {

    private Realm realm ;

    public ArtDB() {}

    /**
     *  把从服务器获得的数据保存在本地realm数据库
     *  @param channel 频道，这个参数是客户端本地的概念，不是服务器端传来的参数
     *  @param rid,
     *  @param data 以JSONArray方式存在的文章数据
     *
     *  下面这个接口假设了服务器回来的数据必须满足
     *  1: data里，每一条数据都包含:inc,rid,tmpl,content,tags,comt,good,bad,share,star
     *  2: 早期，广告是以tmpl=501存放在服务器端上发回来的，我们取消了这一功能，要求广告从客户端生成。
     **/
    public boolean storeArticles(String channel,long rid, JSONArray data, int nomore)
    {
        try{
            realm = Realm.getDefaultInstance();
            long tidmax = 0;
            long tidmin = Long.MAX_VALUE;
            for (int i = 0; i < data.length(); i++) {
                String dataStr = data.get(i).toString();
                JSONObject jsonItem = new JSONObject(dataStr);
                long tid = jsonItem.optLong("inc");
                rid = jsonItem.optLong("rid");
                int tmpl = jsonItem.optInt("tmpl");
                String content  = jsonItem.optString("content");
                String tags     = jsonItem.optString("tags");
                int comt = jsonItem.optInt("comt");
                int good = jsonItem.optInt("good");
                int bad  = jsonItem.optInt("bad");
                int shar = jsonItem.optInt("shar");
                int star = jsonItem.optInt("star");
                long eid = jsonItem.optLong("eid");
                String avatar = jsonItem.optString("avatar");
                String ename = jsonItem.optString("ename");

                if(tid > tidmax) tidmax = tid;
                if(tid < tidmin) tidmin = tid;

                realm.beginTransaction();
                /**
                 *  第一步，如果表中没有
                 *  fixme: 因为缺乏像mysql in这样的查询语句，也缺乏外键这样的机制，
                 *  fixme: 导致我们很难把数据分布在两个表中（一个表方index,一个表方data）
                 *  fixme: 我们目前的方案是，允许一篇文章在artitem表中出现多次
                 * */
                ArtItem dbitem = realm.where(ArtItem.class)
                        .equalTo("tid",tid)
                        .equalTo("channel",channel)
                        .findFirst();
                if(dbitem ==null)
                {
                    dbitem = realm.createObject(ArtItem.class);
                    dbitem.setChannel(channel);
                    dbitem.setRid(rid);
                    dbitem.setTid(tid);
                    dbitem.setContent(content);
                    dbitem.setAvatar(avatar);
                    dbitem.setEid(eid);
                    dbitem.setEname(ename);
                    dbitem.setTags(tags);
                    dbitem.setTmpl(tmpl);
                    dbitem.setChannel(channel);
                    dbitem.setGooded(0);
                    dbitem.setBaded(0);
                    dbitem.setStared(0);
                    Log.e("insert2realm",dbitem.toString());
                }
                /**
                 *  把所有tid的文章都要更新一遍
                 * */
                RealmResults<ArtItem> items = realm.where(ArtItem.class)
                        .equalTo("tid",tid)
                        .findAll();
                for (int k = 0; k < items.size(); k++) {
                    ArtItem item= items.get(k);
                    item.setGood(good);
                    item.setBad(bad);
                    item.setComt(comt);
                    item.setShar(shar);
                    item.setStar(star);
                }
                realm.commitTransaction();
            }
            //System.out.println("---------begin:" );

            realm.beginTransaction();
            ArtViewBlock avb = realm.where(ArtViewBlock.class)
                    .equalTo("channel",channel)
                    .equalTo("rid",rid)
                    .findFirst();

            if(avb == null)
            {
                avb = realm.createObject(ArtViewBlock.class);
                avb.setChannel(channel);
                avb.setRid(rid);
                avb.setTidmin(tidmin);
                avb.setTidmax(tidmax);
                avb = realm.where(ArtViewBlock.class)
                        .equalTo("channel",channel)
                        .findFirst();
                Log.e("postInsertAVB",avb.toString());

            }else{
                if(nomore == 1 )
                {
                    //FIXME: 客户端不应该信任服务器端回来的任何数据，这里的代码需要仔细考量各种情况
                    if(avb.getTidmax() < tidmax)
                        avb.setTidmax(tidmax);

                    if(avb.getTidmin() < tidmin)
                        avb.setTidmin(tidmin);
                }else{
                    avb.setTidmin(tidmin);
                    avb.setTidmax(tidmax);
                }
                Log.e("postUpdateAVB",avb.toString());
            }

            realm.commitTransaction();

        }catch (JSONException e) {
            //System.out.println("---------excep :"+e.toString() );

        }
        return true;
    }

    //获取审核的文章
    public JSONArray loadReviewArticles(long tidref)
    {
        JSONArray result = new JSONArray();
        //realm要求创建realm对象的语句和获取数据的语句必须在同一个线程
        //所以这个要在这个地方getDefaultInstance
        realm = Realm.getDefaultInstance();
        //https://realm.io/docs/swift/latest/#limiting-results
        //上面这个网页解释了为什么realm没有limit这个选项
        RealmResults<ArtItem> items = realm.where(ArtItem.class)
                .equalTo("flag",2)
                .greaterThan("tid",tidref)
                .findAll();
        items.sort("tid",Sort.ASCENDING);

        if (items.size() > 0)
        {
            result.put(items.get(0).getData());
        }
        return result;
    }

    //第一次打开应用
    public JSONArray loadArticles(String channel,String action, long ridref, long tidref, int limit)
    {
        JSONArray result = new JSONArray();
        //realm要求创建realm对象的语句和获取数据的语句必须在同一个线程
        //所以这个要在这个地方getDefaultInstance
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        ArtViewBlock avb = realm.where(ArtViewBlock.class).equalTo("channel",channel).equalTo("rid",ridref).findFirst();
        if(avb != null)
        {
            //https://realm.io/docs/swift/latest/#limiting-results
            //上面这个网页解释了为什么realm没有limit这个选项
            RealmResults<ArtItem> items = null;
            if("load".equals(action))
            {
                items = realm.where(ArtItem.class)
                        .equalTo("channel",channel)
                        .equalTo("rid",ridref)
                        .lessThanOrEqualTo("tid",avb.getTidmax())
                        .greaterThanOrEqualTo("tid",avb.getTidmin())
                        .findAll();
                items.sort("tid",Sort.DESCENDING);
            }else
            if("pull".equals(action))
            {
                items = realm.where(ArtItem.class)
                        .equalTo("channel",channel)
                        .equalTo("rid",ridref)
                        .greaterThan("tid",tidref)
                        .lessThanOrEqualTo("tid",avb.getTidmax())
                        .findAll();
                items.sort("tid",Sort.DESCENDING);
            }else
            if("push".equals(action))
            {
                items = realm.where(ArtItem.class)
                        .equalTo("channel",channel)
                        .equalTo("rid",ridref)
                        .lessThan("tid",tidref)
                        .greaterThanOrEqualTo("tid",avb.getTidmin())
                        .findAll();
                items.sort("tid",Sort.DESCENDING);
            }else
            if("next".equals(action))
            {
                items = realm.where(ArtItem.class)
                        .equalTo("channel",channel)
                        .equalTo("rid",ridref)
                        .greaterThan("tid",tidref)
                        .findAll();
                items.sort("tid",Sort.ASCENDING);
            }else{
                Log.e("ArtDB","不可识别的action: "+action);
            }
            if(items != null)
            {
                limit = items.size() >limit?limit:items.size();
                if(limit > 0)
                {
                    for (int i =  0; i <limit; i++) {
                        result.put(item2json(items.get(i)));
                    }
                }
            }

        }
        realm.commitTransaction();
        return result;
    }

//    //一个特定的只要求某个特定字段的借口
//    public JSONArray loadArticles(String channel,long ridref,long tidmin,long tidmax)
//    {
//        Log.i("ArtDB.loadArticles","Channel = "+channel+" rid = "+ridref+" tidmin = "+tidmin+" tidmax = "+tidmax);
//        JSONArray result = new JSONArray();
//        //realm要求创建realm对象的语句和获取数据的语句必须在同一个线程
//        //所以这个要在这个地方getDefaultInstance
//        realm = Realm.getDefaultInstance();
//        ArtViewBlock avb = realm.where(ArtViewBlock.class).equalTo("channel",channel).equalTo("rid",ridref).findFirst();
//
//        if(avb != null)
//        {
//            //https://realm.io/docs/swift/latest/#limiting-results
//            //上面这个网页解释了为什么realm没有limit这个选项
//            RealmResults<ArtItem> items = realm.where(ArtItem.class)
//                    .equalTo("channel",channel)
//                    .equalTo("rid",ridref)
//                    .lessThanOrEqualTo("tid",tidmax)
//                    .greaterThanOrEqualTo("tid",tidmin)
//                    .findAll();
//
//            items.sort("tid",Sort.DESCENDING);
//            int limit = items.size() ;
//            if(limit > 0)
//            {
//                for (int i =  0; i <limit; i++) {
//                    result.put(items.get(i).getData());
//                }
//            }
//
//        }
//        Log.i("ArtDB.loadArticles","Result = "+result.toString());
//        return result;
//    }

    /*
    * 在数据库中寻找比reftid大的紧连着的20条记录
    *
    * */
//    public JSONArray pullArticles(String channel,long ridref,long tidref,int limit)
//    {
//        JSONArray result = new JSONArray();
//        realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//
//        ArtViewBlock avb = realm.where(ArtViewBlock.class).equalTo("channel",channel).equalTo("rid",ridref).findFirst();
//        if(avb != null)
//        {
//            long tidmax = avb.getTidmax();
//
//            if(tidmax > tidref)
//            {
//                //https://realm.io/docs/swift/latest/#limiting-results
//                //上面这个网页解释了为什么realm没有limit这个选项
//                RealmResults<ArtItem> items = realm.where(ArtItem.class)
//                        .equalTo("channel",channel)
//                        .equalTo("rid",ridref)
//                        .greaterThan("tid",tidref)
//                        .lessThanOrEqualTo("tid",tidmax)
//                        .findAll();
//                items.sort("tid",Sort.DESCENDING);
//                //FIXME:这个地方本来应该做limit,但是做了limit理论上会让这个文章线有断层
//                //FIXME:只能假设，应用处于这样一种状态，落入时间线很旧的地方的时候内存的recycleview还没有这些数据
//                limit = items.size() >limit?limit:items.size();
//
//                if(limit > 0)
//                {
//                    for (int i =  0; i <limit; i++) {
//                        result.put(items.get(i).getData());
//                    }
//                }
//            }
//        }
//        realm.commitTransaction();
//        return result;
//    }

    //翻阅旧的文章
//    public JSONArray pushArticles(String channel,long ridref,long tidref,int limit)
//    {
//        JSONArray result = new JSONArray();
//        realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        ArtViewBlock avb = realm.where(ArtViewBlock.class).equalTo("channel",channel).equalTo("rid",ridref).findFirst();
//        if(avb != null)
//        {
//            long tidmin = avb.getTidmin();
//            if(tidmin < tidref)
//            {
//                //https://realm.io/docs/swift/latest/#limiting-results
//                //上面这个网页解释了为什么realm没有limit这个选项
//                RealmResults<ArtItem> items = realm.where(ArtItem.class)
//                        .equalTo("channel",channel)
//                        .equalTo("rid",ridref)
//                        .lessThan("tid",tidref)
//                        .greaterThanOrEqualTo("tid",tidmin)
//                        .findAll();
//                items.sort("tid",Sort.DESCENDING);
//
//                limit = items.size()>limit?limit:items.size();
//                if(limit > 0)
//                {
//                    for (int i =  0; i <limit; i++) {
//                        result.put(items.get(i).getData());
//                    }
//                }
//            }
//        }
//        realm.commitTransaction();
//        return result;
//    }

//    //翻阅tid比tidref大的紧接着的limit的文章
//    public JSONArray nextArticles(String channel,long ridref,long tidref,int limit)
//    {
//        JSONArray result = new JSONArray();
//        realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//
//                //https://realm.io/docs/swift/latest/#limiting-results
//                //上面这个网页解释了为什么realm没有limit这个选项
//                RealmResults<ArtItem> items = realm.where(ArtItem.class)
//                        .equalTo("channel",channel)
//                        .equalTo("rid",ridref)
//                        .greaterThan("tid",tidref)
//                        .findAll();
//                items.sort("tid",Sort.ASCENDING);
//
//                limit = items.size()>limit?limit:items.size();
//                if(limit > 0)
//                {
//                    for (int i =  0; i <limit; i++) {
//                        result.put(items.get(i).getData());
//                    }
//                }
//
//        realm.commitTransaction();
//        return result;
//    }

    //把tidref对应的文章从本地数据库读出来到一个array中
    public JSONArray loadArticle(long tidref)
    {
        JSONArray result = new JSONArray();
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        int limit = 1;

        //https://realm.io/docs/swift/latest/#limiting-results
        //上面这个网页解释了为什么realm没有limit这个选项
        RealmResults<ArtItem> items = realm.where(ArtItem.class)
                .equalTo("tid",tidref)
                .findAll();
        items.sort("tid",Sort.ASCENDING);

        limit = items.size()>limit?limit:items.size();
        if(limit > 0) {
            for (int i =  0; i <limit; i++) {
                result.put(item2json(items.get(i)));
            }
        }

        realm.commitTransaction();
        return result;
    }

    private JSONObject item2json( ArtItem item)
    {
        JSONObject jo = new JSONObject();
        try{
            jo.put("tid",item.getTid());
            jo.put("rid",item.getRid());
            jo.put("channel",item.getChannel());
            jo.put("content",item.getContent());
            jo.put("avatar",item.getAvatar());
            jo.put("ename",item.getEname());
            jo.put("tmpl",item.getTmpl());
            jo.put("eid",item.getEid());
            jo.put("good",item.getGood());
            jo.put("bad",item.getBad());
            jo.put("shar",item.getShar());
            jo.put("star",item.getStar());
            jo.put("comt",item.getComt());
            jo.put("gooded",item.getGooded());
            jo.put("baded",item.getBaded());
            jo.put("stared",item.getStared());
            jo.put("tags",item.getTags());
        }catch(JSONException e){

        }
        return jo;
    }
}
