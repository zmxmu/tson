package com.syswin.mson;

import com.syswin.Tson.annotation.JsonField;
import com.syswin.Tson.annotation.JsonIgnore;
import com.syswin.Tson.annotation.JsonType;

@JsonType
public class Bean {
    public String name;
    public int age;
    @JsonField("_desc")
    public String description;  //使用JsonField 标注字段在json中的key
    public transient boolean state; //使用transient 不会被序列化
    @JsonIgnore
    public int state2; //使用JsonIgnore注解 不会被序列化

}