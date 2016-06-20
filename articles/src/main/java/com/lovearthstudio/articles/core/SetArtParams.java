package com.lovearthstudio.articles.core;

import java.util.Map;

/**
 * Created by pro on 16/2/23.
 */
public class SetArtParams {

    public long dua_id;
    public long tid;
    public String action;
    public String how;
    public String field;
    public int param;
    public Map<String, Object> filter;

    public class Filter {
        public String key;
        public Object value;

        public Filter(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}


