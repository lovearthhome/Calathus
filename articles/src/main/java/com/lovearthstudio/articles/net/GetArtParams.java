package com.lovearthstudio.articles.net;

import java.util.Map;

/**
 * Created by pro on 16/2/23.
 */
public class GetArtParams {

    public long dua_id;
    public String action;

    public Map<String, Object> filter;

    public String order;
    public String[] fields;

    public int rows;
    public String channel;



    public class Filter {
        public String key;
        public Object value;

        public Filter(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}


