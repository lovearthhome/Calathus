package com.lovearthstudio.articles.net;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public ArtDB() {
        /*
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.clear(ArtViewBlock.class);
        realm.clear(ArtItem.class);
        realm.commitTransaction();
        */

    }

    public boolean storeArticles(String channel, JSONArray data,long tidmax, long tidmin, int nomore)
    {
        try{
            System.out.println("---------beginn:" );
            realm = Realm.getDefaultInstance();

            for (int i = 0; i < data.length(); i++) {

                String dataStr = data.get(i).toString();
                JSONObject jsonItem = new JSONObject(dataStr);
                int tmpl = jsonItem.optInt("tmpl");



                //如果是广告的话，因为没有inc字段，所以在下一条解析inc的时候就出错了，这时候就没有调用后面的callback导致app一直在那旋转等待...
                if(tmpl == 501)
                    continue;
                long tid = jsonItem.getLong("inc");
                realm.beginTransaction();
                ArtItem dbitem = realm.where(ArtItem.class)
                        .equalTo("tid",tid)
                        .equalTo("channel",channel)
                        .findFirst();
                if(dbitem !=null)
                {
                    dbitem.setData(dataStr);
                    Log.e("update2realm",dbitem.toString());
                }else{
                    dbitem = realm.createObject(ArtItem.class);
                    dbitem.setChannel(channel);
                    dbitem.setTid(tid);
                    dbitem.setData(dataStr);
                    Log.e("insert2realm",dbitem.toString());
                }
                realm.commitTransaction();


            }
            System.out.println("---------begin:" );

            realm.beginTransaction();
            ArtViewBlock avb = realm.where(ArtViewBlock.class)
                    .equalTo("channel",channel)
                    .findFirst();

            if(avb == null)
            {
                avb = realm.createObject(ArtViewBlock.class);
                avb.setChannel(channel);
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
            System.out.println("---------excep :"+e.toString() );

        }



        return true;


    }



    //第一次打开应用
    public JSONArray loadArticles(String channel,int limit)
    {
        JSONArray result = new JSONArray();
        //realm要求创建realm对象的语句和获取数据的语句必须在同一个线程
        //所以这个要在这个地方getDefaultInstance
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        ArtViewBlock avb = realm.where(ArtViewBlock.class).equalTo("channel",channel).findFirst();
        if(avb != null)
        {
            //https://realm.io/docs/swift/latest/#limiting-results
            //上面这个网页解释了为什么realm没有limit这个选项
            RealmResults<ArtItem> items = realm.where(ArtItem.class)
                    .equalTo("channel",channel)
                    .lessThanOrEqualTo("tid",avb.getTidmax())
                    .greaterThanOrEqualTo("tid",avb.getTidmin())
                    .findAll();
            items.sort("tid",Sort.DESCENDING);

            limit = items.size() >limit?limit:items.size();
            if(limit > 0)
            {
                for (int i =  0; i <limit; i++) {
                    result.put(items.get(i).getData());
                }
            }

        }
        realm.commitTransaction();
        return result;
    }

    //一个特定的只要求某个特定字段的借口
    public JSONArray loadArticles(String channel,long tidmin,long tidmax)
    {
        JSONArray result = new JSONArray();
        //realm要求创建realm对象的语句和获取数据的语句必须在同一个线程
        //所以这个要在这个地方getDefaultInstance
        realm = Realm.getDefaultInstance();
        ArtViewBlock avb = realm.where(ArtViewBlock.class).equalTo("channel",channel).findFirst();

        if(avb != null)
        {
            //https://realm.io/docs/swift/latest/#limiting-results
            //上面这个网页解释了为什么realm没有limit这个选项
            RealmResults<ArtItem> items = realm.where(ArtItem.class)
                    .equalTo("channel",channel)
                    .lessThanOrEqualTo("tid",tidmax)
                    .greaterThanOrEqualTo("tid",tidmin)
                    .findAll();

            items.sort("tid",Sort.DESCENDING);
            int limit = items.size() ;
            if(limit > 0)
            {
                for (int i =  0; i <limit; i++) {
                    result.put(items.get(i).getData());
                }
            }

        }

        return result;
    }

    /*
    * 在数据库中寻找比reftid大的紧连着的20条记录
    *
    * */
    public JSONArray pullArticles(String channel,long tidref,int limit)
    {
        JSONArray result = new JSONArray();
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        ArtViewBlock avb = realm.where(ArtViewBlock.class).equalTo("channel",channel).findFirst();
        if(avb != null)
        {
            long tidmax = avb.getTidmax();

            if(tidmax > tidref)
            {
                //https://realm.io/docs/swift/latest/#limiting-results
                //上面这个网页解释了为什么realm没有limit这个选项
                RealmResults<ArtItem> items = realm.where(ArtItem.class)
                        .equalTo("channel",channel)
                        .greaterThan("tid",tidref)
                        .lessThanOrEqualTo("tid",tidmax)
                        .findAll();
                items.sort("tid",Sort.DESCENDING);
                //FIXME:这个地方本来应该做limit,但是做了limit理论上会让这个文章线有断层
                //FIXME:只能假设，应用处于这样一种状态，落入时间线很旧的地方的时候内存的recycleview还没有这些数据
                limit = items.size() >limit?limit:items.size();

                if(limit > 0)
                {
                    for (int i =  0; i <limit; i++) {
                        result.put(items.get(i).getData());
                    }
                }
            }
        }
        realm.commitTransaction();
        return result;
    }

    //翻阅旧的文章
    public JSONArray pushArticles(String channel,long tidref,int limit)
    {
        JSONArray result = new JSONArray();
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        ArtViewBlock avb = realm.where(ArtViewBlock.class).equalTo("channel",channel).findFirst();
        if(avb != null)
        {
            long tidmin = avb.getTidmin();
            if(tidmin < tidref)
            {
                //https://realm.io/docs/swift/latest/#limiting-results
                //上面这个网页解释了为什么realm没有limit这个选项
                RealmResults<ArtItem> items = realm.where(ArtItem.class)
                        .equalTo("channel",channel)
                        .lessThan("tid",tidref)
                        .greaterThanOrEqualTo("tid",tidmin)
                        .findAll();
                items.sort("tid",Sort.DESCENDING);

                limit = items.size()>limit?limit:items.size();
                if(limit > 0)
                {
                    for (int i =  0; i <limit; i++) {
                        result.put(items.get(i).getData());
                    }
                }
            }
        }
        realm.commitTransaction();
        return result;
    }
}
