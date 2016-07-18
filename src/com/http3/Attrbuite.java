    package com.http3;  
    /*	http://blog.csdn.net/ld191474639/article/details/7989772
     *  java网络爬虫——获取页面的所有超链接的内容 
     */
      
    import java.util.ArrayList;  
    import java.util.List;  
      
      
    import org.htmlparser.Node;  
    import org.htmlparser.NodeFilter;  
    import org.htmlparser.Parser;  
      
    import org.htmlparser.nodes.TagNode;  
    import org.htmlparser.util.NodeList;  
    import org.htmlparser.util.ParserException;  
      
    /**  
     * 提取具有某个属性值的标签列表  
     * @author Shane
     *  http://blog.csdn.net/ld191474639/article/details/7989772
     */  
      
    public class Attrbuite {  
    public static <T extends TagNode> List<T> getText(String html,final Class<?> tagType,final String attrName,final String attrValue){  
        try{  
            Parser p=new Parser();  
            p.setInputHTML(html);  
            NodeList list=p.parse(new NodeFilter(){  
      
                public boolean accept(Node node) {  
                    if(node.getClass()==tagType){  
                        T t=(T)node;  
                        if(attrName==null){  
                            return true;  
                        }  
                        String attrValue=t.getAttribute("luanma:"+attrName);  
                        System.out.println(attrValue);  
                        if(attrValue!=null&&attrValue.equals(attrValue)){  
                            return true;  
                        }  
                    }  
                    return false;  
                }  
                  
            }  
              
            );  
        List<T> tags=new ArrayList<T>();  
        for(int i=0;i<list.size();i++){  
            T tt=(T)list.elementAt(i);  
            tags.add(tt);  
        }  
        return tags;  
        }catch(Exception e){  
            e.printStackTrace();  
        }return null;  
    }  
    public static <T extends TagNode> List<T> getText(String html,final Class<?> tagType){  
    return getText( html,  tagType,null,null);  
    }  
    }  