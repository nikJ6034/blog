package com.nikj.blog.crawling;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Blog {
    
    public void test() {
        String url = "https://blog.naver.com/PostView.nhn?blogId=spkyk&logNo=1006895&redirect=Dlog&widgetTypeCall=true&directAccess=false"; //크롤링할 url지정
        Document doc = null;        //Document에는 페이지의 전체 소스가 저장된다
        System.out.println("실행");
        try {
            doc = Jsoup.connect(url).get();
            
            Elements elem = doc.select(".se_title"); 
            
            String str = elem.text(); 
            
            Elements content = doc.select(".__se_component_area"); 
            
            System.out.println(str);
            
            System.out.println(content.text());

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("끝");
    }
}
