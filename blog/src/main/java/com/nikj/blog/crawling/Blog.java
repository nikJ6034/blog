package com.nikj.blog.crawling;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.nikj.blog.entity.Bbs;
import com.nikj.blog.entity.Links;
import com.nikj.blog.repository.BbsRepository;
import com.nikj.blog.repository.LinksRepository;

@Component
public class Blog implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Autowired
	private LinksRepository linksRepository;
	@Autowired
	private BbsRepository bbsRepository;
    
//	@Bean
    public void test() {
        Document doc = null;        //Document에는 페이지의 전체 소스가 저장된다
        System.out.println("실행");
        ObjectMapper objectMapper = new ObjectMapper();
        
        
        	
        
        try {
        	for(int i = 1; i < 200;i++) {
        		String url2 = "https://blog.naver.com/PostTitleListAsync.nhn?blogId=spkyk&viewdate=&currentPage="+i+"&categoryNo=&parentCategoryNo=&countPerPage=10";
	            doc = Jsoup.connect(url2).get();
	            
	            String text = doc.text();
	            
	            Map<String, Object> readValue = objectMapper.readValue(text, Map.class);
	            
	            List<Map<String, Object>> list = (List<Map<String, Object>>)readValue.get("postList");
	            
	            for(Map<String, Object> value : list) {
	            	
	            	Links links = new Links();
	            	links.setLink((String)value.get("logNo"));
	            	links.setAddDate((String)value.get("addDate"));
	            	linksRepository.saveAndFlush(links);
	            	
	            }
            
        	}
            
            //Map fromJson = gson.fromJson("{\"logNo\":\"221353754732\",\"title\":\"test\"}", Map.class);
//            
//            System.out.println(fromJson);
            
//            Elements elem = doc.select(".se_title"); 
//            
//            String str = elem.text(); 
//            
//            Elements content = doc.select(".__se_component_area"); 
//            
//            System.out.println(str);
//            
//            System.out.println(content.text());

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        System.out.println("끝");
    }
    
//    @Bean
    public void craw() {
    	
    	List<Links> findAll = linksRepository.findAll();
    	Document doc = null;        //Document에는 페이지의 전체 소스가 저장된다
    	
    	for(Links links : findAll) {
	    	try{
	        		String url = "https://blog.naver.com/PostView.nhn?blogId=spkyk&logNo="+links.getLink()+"&redirect=Dlog&widgetTypeCall=true&directAccess=false"; //크롤링할 url지정
	        		doc = Jsoup.connect(url).get();
	        		Elements titleEl = doc.select(".se_title"); 
	        	    Elements contentEl = doc.select(".__se_component_area"); 
	        	     String title = null;
	        	     String content = null;
	        	    
	        	    if(titleEl.hasText()) {
	        	    	title = titleEl.text();
	        	    }else{
	        	    	titleEl = doc.select("#title_1").select(".pcol1");
	        	    }
	        	    
	        	    if(contentEl.hasText()) {
	        	    	content = contentEl.text();
	        	    }else {
	        	    	contentEl = doc.select("#postViewArea");
	        	    }
	        	    
	        	    
	        	    title = titleEl.text();
	        	    content = contentEl.text();
	        	    Bbs bbs = new Bbs();
	        	    bbs.setBbsType("blog");
	        	    bbs.setTitle(title);
	        	    bbs.setContent(content);
	        	    bbs.setContentId(links.getLink());
	        	    
	        	    bbsRepository.save(bbs);
	    	}catch (Exception e) {
	    		System.out.println(links.getLink());
				// TODO: handle exception
			}
    	}
    	
    	
	    
	    
	    
    }
}
