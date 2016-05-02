package com.lovearthstudio.articles.net;


import java.util.List;

/**
 * Created by pro on 16/2/23.
 */
public class IndexResponse {


    /**
     * status : 0
     * reason : Success
     * result : [{"inc":"94832","brief":null,"star":"0","comt":"0","content":"\n\n我在学校做了一个噩梦，一手拍着桌子大叫：啊！老师笑着走过来，问，怎么了，我说：我做了个噩梦。老师说，孩子，别怕，噩梦才刚刚开始\u2026\u2026\n<!--1459276588-->\n\n","title":null,"good":"0","bad":"0","shar":"0","eid":"4","editor_name":"易明雨","editor_sex":"M","editor_avatar":"http://avatar.xdua.org/2016040183a703a15270d93ec34b724249450c5e.jpg"},{"inc":"94831","brief":null,"star":"0","comt":"0","content":"\n\n毒疫苗案！脸都丢到国外去了！<br>世界卫生组织就中国疫苗事件召开记者会。世卫组织驻华办事处免疫规划项目组组长兰斯医生表示，中国民众没有必要赴海外打疫苗，世卫组织对中国疫苗生产许可有信心。\n<!--1459271560-->\n\n","title":null,"good":"0","bad":"0","shar":"0","eid":"3","editor_name":"追逐","editor_sex":"F","editor_avatar":"http://avatar.xdua.org/2016040182a703a15270d93ec34b724249450c5e.png"},{"inc":"94830","brief":null,"star":"0","comt":"0","content":"\n\n今天和老婆一前一后的上楼梯，我就用俩手扶住了老婆的屁屁，老婆说，拿开手，不然放个屁崩你。我看了眼老婆说，没事，放吧，你穿的皮裤，放个屁也散不出来！<br>老婆笑的差点滚下楼梯呵呵\n<!--1459277376-->\n\n","title":null,"good":"0","bad":"0","shar":"0","eid":"2","editor_name":"辣椒帅","editor_sex":"M","editor_avatar":"http://avatar.xdua.org/2016040181a703a15270d93ec34b724249450c5e.jpeg"},{"inc":"94829","brief":null,"star":"0","comt":"0","content":"\n\n今天听到这样的回话<br>A：最近干嘛？<br>B回：最近很忙，没时间\u2026<br>我好像明白了点什么\n<!--1459254893-->\n\n","title":null,"good":"0","bad":"0","shar":"0","eid":"2","editor_name":"辣椒帅","editor_sex":"M","editor_avatar":"http://avatar.xdua.org/2016040181a703a15270d93ec34b724249450c5e.jpeg"},{"inc":"94828","brief":null,"star":"0","comt":"0","content":"\n\n多少次人们制定出完美的人生规划，而最终却偏离了最初的本心，再也回不到原点\u2026而人生也只能被无奈的重新洗牌！\n<!--1459272101-->\n\n","title":null,"good":"0","bad":"0","shar":"0","eid":"2","editor_name":"辣椒帅","editor_sex":"M","editor_avatar":"http://avatar.xdua.org/2016040181a703a15270d93ec34b724249450c5e.jpeg"}]
     */

    private int status;
    private String reason;
    /**
     * inc : 94832
     * brief : null
     * star : 0
     * comt : 0
     * content :
     * <p>
     * 我在学校做了一个噩梦，一手拍着桌子大叫：啊！老师笑着走过来，问，怎么了，我说：我做了个噩梦。老师说，孩子，别怕，噩梦才刚刚开始……
     * <!--1459276588-->
     * <p>
     * <p>
     * title : null
     * good : 0
     * bad : 0
     * shar : 0
     * eid : 4
     * editor_name : 易明雨
     * editor_sex : M
     * editor_avatar : http://avatar.xdua.org/2016040183a703a15270d93ec34b724249450c5e.jpg
     */

    private List<ResultBean> result;

    @Override
    public String toString() {
        return "IndexResponse{" +
                "status=" + status +
                ", reason='" + reason + '\'' +
                ", result=" + result +
                '}';
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        public int typeId;
        private String inc;
        private Object brief;
        private String star;
        private String comt;
        private String content;
        private Object title;
        private String good;
        private String bad;
        private String shar;
        private String eid;
        private String editor_name;
        private String editor_sex;
        private String editor_avatar;

        public String getInc() {
            return inc;
        }

        public void setInc(String inc) {
            this.inc = inc;
        }

        public Object getBrief() {
            return brief;
        }

        public void setBrief(Object brief) {
            this.brief = brief;
        }

        public String getStar() {
            return star;
        }

        public void setStar(String star) {
            this.star = star;
        }

        public String getComt() {
            return comt;
        }

        public void setComt(String comt) {
            this.comt = comt;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Object getTitle() {
            return title;
        }

        public void setTitle(Object title) {
            this.title = title;
        }

        public String getGood() {
            return good;
        }

        public void setGood(String good) {
            this.good = good;
        }

        public String getBad() {
            return bad;
        }

        public void setBad(String bad) {
            this.bad = bad;
        }

        public String getShar() {
            return shar;
        }

        public void setShar(String shar) {
            this.shar = shar;
        }

        public String getEid() {
            return eid;
        }

        public void setEid(String eid) {
            this.eid = eid;
        }

        public String getEditor_name() {
            return editor_name;
        }

        public void setEditor_name(String editor_name) {
            this.editor_name = editor_name;
        }

        public String getEditor_sex() {
            return editor_sex;
        }

        public void setEditor_sex(String editor_sex) {
            this.editor_sex = editor_sex;
        }

        public String getEditor_avatar() {
            return editor_avatar;
        }

        public void setEditor_avatar(String editor_avatar) {
            this.editor_avatar = editor_avatar;
        }

        @Override
        public String toString() {
            return "ResultBean{" +
                    "inc='" + inc + '\'' +
                    ", brief=" + brief +
                    ", star='" + star + '\'' +
                    ", comt='" + comt + '\'' +
                    ", content='" + content + '\'' +
                    ", title=" + title +
                    ", good='" + good + '\'' +
                    ", bad='" + bad + '\'' +
                    ", shar='" + shar + '\'' +
                    ", eid='" + eid + '\'' +
                    ", editor_name='" + editor_name + '\'' +
                    ", editor_sex='" + editor_sex + '\'' +
                    ", editor_avatar='" + editor_avatar + '\'' +
                    '}';
        }
    }
}
