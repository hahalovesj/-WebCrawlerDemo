    package com.http4;  
    /*	http://blog.csdn.net/ld191474639/article/details/7989772
     *  java网络爬虫——获取页面的所有超链接的内容 
     */
      
    import java.io.BufferedReader;  
    import java.io.InputStream;  
    import java.io.InputStreamReader;  
    import java.net.URI;  
    import java.net.URL;  
    import java.util.List;  
      
    import org.apache.http.HttpEntity;  
    import org.apache.http.HttpResponse;  
    import org.apache.http.client.HttpClient;  
    import org.apache.http.client.methods.HttpGet;  
    import org.apache.http.impl.client.DefaultHttpClient;  
    import org.apache.http.util.EntityUtils;  
    import org.htmlparser.tags.LinkTag;  
      
    import com.http3.*;  
    public class Url2_aoyou {  
      
        /**  
         * @param args  
         */  
        public static void main(String[] args) {  
            try{  
                HttpClient http=new DefaultHttpClient();  
                //http.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("172.17.18.84",8080));  
                  
                HttpGet hg=new HttpGet("http://www.aoyou.com/");  
                HttpResponse hr=http.execute(hg);  
                HttpEntity he=hr.getEntity();//httpEntity是用来表征一个http报文的实体，用来发送或接收。
                if(he!=null){  
                    String charset=EntityUtils.getContentCharSet(he);  
                  
                    InputStream is=he.getContent();  
                    BufferedReader br=new BufferedReader(new InputStreamReader(is,"utf-8"));  
                    String line=null;  
                    //IOUtils.copy(is,new FileOutputStream("E:/Baidu.html"));  
                while((line=br.readLine())!=null){  
                        List<LinkTag> link=Attrbuite.getText(line, LinkTag.class);  
                        for(LinkTag l:link){  
                            System.out.println(l.getStringText());  
                            System.out.println(l.getLink());
                        }  
                    }  
                    is.close();  
                }  
                http.getConnectionManager().shutdown();  
            }catch(Exception e){  
                e.printStackTrace();  
            }  
      
        }  
      
    }  