package com.learn.chapter09.util;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ContentHandler extends DefaultHandler {
    private String nodeName;

    private StringBuilder id;

    private StringBuilder name;

    private StringBuilder version;

    //        初始化要解析的节点内容参数为StringBuilder
    @Override
    public void startDocument() throws SAXException {
        id = new StringBuilder();
        name = new StringBuilder();
        version = new StringBuilder();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
//开始解析某个节点，localName当前节点名字
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//        记录当前节点名
        nodeName = localName;
    }
//判断节点解析完成
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if("app".equals(localName)){
//            去除空格和换行符\n
            Log.d("ContentHandler","id is "+id.toString().trim());
            Log.d("ContentHandler","name is "+name.toString().trim());
            Log.d("ContentHandler","version is "+version.toString().trim());
//            最后要将StringBuilder清除
            id.setLength(0);
            name.setLength(0);
            version.setLength(0);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
//        根据当前的节点名判断将内容添加到哪一个StringBuilder对象中
        if("id".equals(nodeName)){
            id.append(ch,start,length);
        }else if("name".equals(nodeName)){
            name.append(ch, start, length);
        }else if("version".equals(nodeName)){
            version.append(ch, start, length);
        }
    }


}
