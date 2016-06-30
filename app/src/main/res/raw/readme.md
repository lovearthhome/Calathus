#layout说明

##本项目采用的laytout

###card_wrapper: 分为三种。一种有底部工具条的，一种没有底部工具条，但都有右上侧的"更多"按钮，
###一个wrapper默认包含一个holder。holder布局嵌套在container布局的@+id/root_layout里面，这个嵌套是由java代码进行的

    1. card_wrapper_ad.xml  右上侧是更多
    2. card_wrapper_cmt.xml 右上侧是点赞
    3. card_wrapper_art.xml 下面有一个operator
    
###holder: holder本身是android概念，在这里被借用于表达包含card的容器,holder仅仅是个方块，不包含额外的布局.它没有operator
###这个Holder的存在还是必要的，将来可以方便的扩展，比如，card组合.


###card: 每种类型的数据的基本布局