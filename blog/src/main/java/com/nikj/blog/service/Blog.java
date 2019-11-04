package com.nikj.blog.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nikj.blog.entity.Bbs;
import com.nikj.blog.entity.BbsWord;
import com.nikj.blog.entity.BbsWordId;
import com.nikj.blog.entity.Links;
import com.nikj.blog.entity.Word;
import com.nikj.blog.repository.BbsRepository;
import com.nikj.blog.repository.BbsWordRepository;
import com.nikj.blog.repository.LinksRepository;
import com.nikj.blog.repository.WordRepository;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

@Service
@Transactional
public class Blog{

    @Autowired
    private LinksRepository linksRepository;
    @Autowired
    private BbsRepository bbsRepository;
    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private BbsWordRepository bbsWordRepository;
    
    public String test2() {
        System.out.println("실행됐다 test2");
        return "테스트 입니다.";
    }
    
    public List<Links> test3() {
        List<Links> findAll = linksRepository.findAll();
        System.out.println("실행됐다 test3");
        return findAll;
    }

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

    public String komo() {
        
        List<Bbs> findAll = bbsRepository.findAll();
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        
        findAll.stream().forEach(one ->{
            
            String strToAnalyze = one.getTitle()+" "+one.getContent();

            KomoranResult analyzeResultList = komoran.analyze(strToAnalyze);

            List<Token> tokenList = analyzeResultList.getTokenList();
            for (Token token : tokenList) {
                Optional<Word> findByWordAndPos = wordRepository.findByWordAndPos(token.getMorph(), token.getPos());
                BbsWordId bbsWordId = new BbsWordId();
                Word save = null;
                bbsWordId.setBbs(one.getId());
                if(findByWordAndPos.isPresent()) {
                    bbsWordId.setWord(findByWordAndPos.get().getId());
                }else {
                    Word word = new Word();
                    word.setWord(token.getMorph());
                    word.setPos(token.getPos());
                    save = wordRepository.save(word);
                    bbsWordId.setWord(save.getId());
                }
                
                Optional<BbsWord> findById = bbsWordRepository.findById(bbsWordId);
                
                if(findById.isPresent()) {
                    findById.ifPresent(bbsWord ->bbsWord.setCount(bbsWord.getCount()+1));
                }else {
                    BbsWord bbsWord = new BbsWord();
                    Word word2 = new Word();
                    word2.setId(bbsWordId.getWord());
                    bbsWord.setBbs(one);
                    bbsWord.setWord(word2);
                    bbsWord.setCount(1);
                    bbsWordRepository.save(bbsWord);
                }
                
            }
            System.out.println(one.getId());
        });
        
        return "success";
    }
}
