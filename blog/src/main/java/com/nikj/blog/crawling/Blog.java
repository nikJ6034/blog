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

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

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

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        System.out.println("끝");
    }
    
//    @Bean
    public void craw() {
    	
    	List<Links> findAll = linksRepository.findAll();
//      Optional<Links> findById = linksRepository.findById(1);
//    	Links links = findById.get();
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
	        	    content = contentEl.html();
	        	    content = content.replaceAll("<br>", "\n");
	        	    content = content.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	        	    content = content.replaceAll("<!--.*-->", "");
	        	    content = content.replaceAll("<o:p></o:p>", "");
	        	    content = content.replaceAll("<?xml:namespace.*/>", "");
	        	    
	        	    content = content.replaceAll("<h5.*>", "");
	        	    content = content.replaceAll("<h1.*>", "");
	        	    content = content.replaceAll("<h2.*>", "");
	        	    content = content.replaceAll("<v:shapetype.*>", "");
	        	    content = content.replaceAll("</h5>", "");
	        	    content = content.replaceAll("</h1>", "");
	        	    content = content.replaceAll("</h2>", "");
	                content = content.replaceAll("</v:shapetype>", "");
	        	    content = content.replaceAll("^\\s*", "");
	        	    content = content.replaceAll("\\s*$", "");
	        	    content = content.replaceAll("&lt;", "<");
	        	    content = content.replaceAll("&gt;", ">");
	        	    content = content.replaceAll("&amp;", "&");
	        	    content = content.replaceAll("&nbsp;", " ");
	        	    
	        	    Bbs bbs = new Bbs();
	        	    bbs.setBbsType("blog");
	        	    bbs.setTitle(title);
	        	    bbs.setContent(content);
	        	    bbs.setContentId(links.getLink());

	        	    bbsRepository.save(bbs);
	    	}catch (Exception e) {
	    		System.out.println(links.getLink());
			}
    	}
	    
    }
    
    @Bean
    public void komo() {
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        String strToAnalyze = "‘평화’의 가파른 언덕을 오르며 맞이하는 ‘추석’입니다. 이번 보름달에게는 우리 모두 ‘평화로운 한반도’를 빌어보면 좋겠습니다. 평화의 온기 속에서 우리 모두 편안하고 행복한 추석되시길 바랍니다.";
        
        KomoranResult analyzeResultList = komoran.analyze(strToAnalyze);

        System.out.println(analyzeResultList.getPlainText());

        List<Token> tokenList = analyzeResultList.getTokenList();
        for (Token token : tokenList) {
            System.out.format("(%2d, %2d) %s/%s\n", token.getBeginIndex(), token.getEndIndex(), token.getMorph(), token.getPos());
        }
    }
}
