package com.example.demo.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Comparator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

//upload
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.Model;
import com.example.demo.UploadingSetting.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.lang.NullPointerException;
import java.util.Arrays;
import java.io.OutputStream;

//OCR
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.net.URI;

//repository
import com.example.demo.repository.RegisterwordRepository;
import com.example.demo.model.Chainwords;
import com.example.demo.model.Registerword;
import com.example.demo.model.Reminder;
import com.example.demo.model.SearchOption;
import com.example.demo.repository.WordsILearnedRepository;
import com.example.demo.model.WordsILearned;

@RequiredArgsConstructor
@Controller
public class WordController {
	
	private final RegisterwordRepository repository;
	private final WordsILearnedRepository repository_learned;

	//for reading page
	String[] selectwords = new String[500];
	
	//for handing over word
	String new_word;
	
	//for moving to page
	String move;
	String move_id;
	
	Long idL;
	

	
	boolean redirect_case = true;

	
	String border;
	int counter_fromborder;
	
	@GetMapping("/")
	public String Select(Model model,Model model2, Reminder reminder){
		
		LocalDate border_localdate = LocalDate.now().minusDays(7);
		
		//convert LocalDate to String
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		border = border_localdate.format(formatter);
		
		counter_fromborder = repository.findByDateGreaterThan(border).size();
		
		
		reminder.setCounter(counter_fromborder);
		model.addAttribute("reminder", reminder);
		model2.addAttribute("review", repository.findByDateGreaterThan(border));
		
		
		return "select";
	}
	
	@GetMapping("/home")
	public String Home(){
		return "home";
	}
	
	@GetMapping("/register")
	public String Register() {
		return "register";
	}
	
	//convert picture into words
	@RequestMapping(path = "/upload", method = RequestMethod.GET)
	String UploadView() {
		return "upload";
	}
	
	@RequestMapping(path = "/upload", method = RequestMethod.POST)
	String Upload( UploadingSetting uploadsetting) {
		
		if(uploadsetting.getFile().isEmpty()) {
			return "upload";
		}
		
		Path path = Paths.get("C:/Users/user/Documents/workspace-spring-tool-suite-4-4.7.0.RELEASE/spring-examination/image");
		if(!Files.exists(path)) {
			try {
				Files.createDirectory(path);
			}catch(NoSuchFileException e) {
				System.out.println(e);
			}catch(IOException e) {
				System.out.println(e);
			}
		}
		
		int dot = uploadsetting.getFile().getOriginalFilename().lastIndexOf(".");
		String extension = "";
		
		if(dot > 0) {
			extension = uploadsetting.getFile().getOriginalFilename().substring(dot).toLowerCase();
		}
		System.out.println(extension);
		String filename = "1";
		Path uploadpath = Paths.get("C:/Users/user/Documents/workspace-spring-tool-suite-4-4.7.0.RELEASE/spring-examination/image/" + filename + extension);
		
		//Now, can't use JPEG
		if(!extension.equals(".png")) {
			System.out.println("png以外には対応していません");
			return "upload";
		}
		
		try(OutputStream os = Files.newOutputStream(uploadpath, StandardOpenOption.CREATE)){
			byte[] bytes = uploadsetting.getFile().getBytes();
			os.write(bytes);
		}catch(IOException e) {
			System.out.println(e);
		}
		
	
		URI Newuploadpath = uploadpath.toUri();
	//OCR　codes
		if(Files.exists(uploadpath)) {
		try {
			File file = new File(Newuploadpath);
			BufferedImage img = ImageIO.read(file);
			
			ITesseract tesseract = new Tesseract();		
			tesseract.setDatapath("src/main/resources/templates/");
			
			String str = tesseract.doOCR(img);
			
		//get words and arrangement
			String[] newstr = str.split("[^a-zA-Z1-9]+");
			selectwords = newstr;
			file.delete();
			
		}catch(IOException e){
			//if this error happened , "errors" will be true and "message" will be each error message.
			
			System.out.println("画像がありません");
			return "redirect:/upload";
		}catch(TesseractException e) {
			
			System.out.println("bppが64のため、読み取れませんでした。他の画像を試してください");
			return "redirect:/upload";
		}catch(NullPointerException e) {
			
			System.out.println("bppが64のため、読み取れませんでした。他の画像を試してください");
			return "redirect:/upload";
		}
		
		}else {
			System.out.println("予想外のエラー");
		}
		return "redirect:/selectword/select";
	}
	
	//select words
	@GetMapping("/selectword/select")
	public String SelectWord(@ModelAttribute Chainwords chainwords , Model model) {
		model.addAttribute("selectword", selectwords);
		return "selectword";
	}
	
	//post words and chain it for creating a word
	@PostMapping("/selectword")
	public String selectword(@Validated @ModelAttribute Chainwords chainwords, BindingResult result) {
		if(result.hasErrors()) {
			return "upload";
		}
		new_word = chainwords.getParts().replaceAll(",", " ");
		
		int count_w = 0;
		for(int i=0; i < repository.findByWordIs(new_word).size(); i++) {
			count_w++;
		}
		
		if(count_w>=1) {
			System.out.println("リダイレクトで該当する単語のページに飛びます");
			redirect_case=false;
			return "redirect:/word/"+ new_word;
		}else {
		
		return "redirect:/submitnow/get";
		}
	}
	
	@GetMapping("/submitnow/get")
	public String SubmitNow(@ModelAttribute Registerword registerword) {
		if(new_word != null) {
			registerword.setWord(new_word);
		}else {
			registerword.setWord("");
		}
		return "submitnow";
	}
	
	@PostMapping("/submitnow")
	public String SubmitNowPost(@ModelAttribute Registerword registerword) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate now = LocalDate.now();
		registerword.setDate(now.format(formatter));
		
		if(registerword.getDate() == null || registerword.getMean()==null || registerword.getPartofspeech() ==null||
				registerword.getWord()==null ) {
			System.out.println("登録できません");
			return "redirect:/selectword/select";
		}
		
		repository.save(registerword);
		
		if(new_word != null) {
			return "redirect:/selectword/select";
		}else {
			return "redirect:/submitnow/get";
		}
	}
	
	
	//sort & search
	@RequestMapping(path = "/list", method = { RequestMethod.GET , RequestMethod.POST })
	public String List(SearchOption searchoption , Model model, Model model2) {
		
		
		
		model2.addAttribute("searchoption", searchoption);
		
		
		//sort , search and move
		//String change_list = searchoption.getOption_main() + searchoption.getOption_sub();
		String adjective = searchoption.getOption_main();
		
		
		String search_list = searchoption.getSearchword_s();
		String searchdate_list = searchoption.getSearchdate_s();
		

		if(searchoption.getToEdit().equals("初期値") && searchoption.getToMove() == null) {
			
			if(searchoption.getOption_main().equals("無")) {
			
				if(searchoption.getOption_sub().equals("0")) {
					if(search_list == null && searchdate_list == null) {
						model.addAttribute("lists", repository.findAll());
					}else if( search_list == null &&searchdate_list != null ){
						model.addAttribute("lists", repository.findByDateLike("%"+searchdate_list+ "%"));	
					}else if(search_list != null && searchdate_list== null) {
						model.addAttribute("lists", repository.findByWordLike("%"+search_list+ "%"));
					}else {
						model.addAttribute("lists", repository.findByWordLikeAndDateLike("%"+search_list+ "%", "%"+searchdate_list+ "%"));
					}
					
				}
				if(searchoption.getOption_sub().equals("1")) {
					if(search_list == null && searchdate_list == null) {
						model.addAttribute("lists", repository.findByOrderByWord());
					}else if( search_list == null && searchdate_list != null ){
						model.addAttribute("lists", repository.findByDateLikeOrderByWord("%"+searchdate_list+ "%"));	
					}else if(search_list != null && searchdate_list== null) {
						model.addAttribute("lists", repository.findByWordLikeOrderByWord("%"+search_list+ "%"));
					}else {
						model.addAttribute("lists", repository.findByWordLikeAndDateLikeOrderByWord("%"+search_list+ "%", "%"+searchdate_list+ "%"));
					}
				}
				return "list";
			
			}else {

				if(searchoption.getOption_sub().equals("0")) {
					if(search_list == null && searchdate_list == null) {
						model.addAttribute("lists", repository.findByPartofspeechLike("%" + adjective + "%"));
					}else if(search_list != null && searchdate_list == null) {
						model.addAttribute("lists", repository.findByWordLikeAndPartofspeechLike("%"+search_list+ "%", "%" + adjective + "%"));
					}else if(search_list == null && searchdate_list != null) {
						model.addAttribute("lists", repository.findByDateLikeAndPartofspeechLike("%"+searchdate_list+ "%", "%" + adjective + "%"));
					}else {
						model.addAttribute("lists", repository.findByDateLikeAndWordLikeAndPartofspeechLike("%"+searchdate_list+ "%", "%"+search_list+ "%" , "%" + adjective + "%"));
								}
				}else {
					if(search_list == null && searchdate_list == null) {
						model.addAttribute("lists", repository.findByPartofspeechLikeOrderByWord("%" + adjective + "%"));
					}else if(search_list != null && searchdate_list == null) {
						model.addAttribute("lists", repository.findByPartofspeechLikeAndWordLikeOrderByWord("%" + adjective + "%", "%"+search_list+ "%"));
					}else if(search_list == null && searchdate_list != null) {
						model.addAttribute("lists", repository.findByPartofspeechLikeAndDateLikeOrderByWord("%" + adjective + "%", "%"+searchdate_list+ "%"));
					}else {
						model.addAttribute("lists", repository.findByPartofspeechLikeAndDateLikeAndWordLikeOrderByWord("%" + adjective + "%", "%"+searchdate_list+ "%", "%"+search_list+ "%"));
							}
				}
				return "list";
			}
				}else if(!searchoption.getToEdit().equals("初期値") && searchoption.getToMove()==null){
			//move to a word
					move = searchoption.getToEdit();
					return "redirect:/word/"+ move ;
			
				}else if(searchoption.getToEdit().equals("初期値") && searchoption.getToMove() != null) {
			
					idL = Long.parseLong(searchoption.getToMove());
			
				return "redirect:/edit&move/" + idL;
				}else {
					System.out.println("この条件は適用できません");
					return "home";
		}	
	}
	
	@RequestMapping(path = "/word/{word}", method ={RequestMethod.GET , RequestMethod.POST})
	public String WordMove(@PathVariable String word,  Model model, SearchOption searchoption, Model model2) {
		if(redirect_case == true) {
		model.addAttribute("edit_word", repository.findByWordIs(move));
		model2.addAttribute("searchoption", searchoption);
		}else {
	// from selectword.html
		model.addAttribute("edit_word", repository.findByWordIs(new_word));
		model2.addAttribute("searchoption", searchoption);
		}
		
		if(searchoption.getToId() != null) {
			move_id = searchoption.getToId();
			return "redirect:/word_edit/" + move_id;
		}else {
		return "word";
		}
	}
	
	@Transactional
	@GetMapping("/delete/{word}")
	public String WordDelete(@PathVariable String word) {
		repository.deleteByWordIs(move);
		return "redirect:/list";
	}
	
	@GetMapping("/word_edit/{id}" )
	public String WordEdit(@PathVariable Long id, Model model) {
		model.addAttribute("registerword", repository.findById(id));
				return "/word_edit";
	}
	
	@Transactional
	@PostMapping("/word_edit/post")
	public String WordEdit( @ModelAttribute Registerword registerword) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate now = LocalDate.now();
		registerword.setDate(now.format(formatter));
		
		if(registerword.getDate() == null || registerword.getMean()==null || registerword.getPartofspeech() ==null||
				registerword.getWord()==null ) {
			System.out.println("変更不可");
			return "list";
		}
		
		repository.deleteByWordIs(move);
		repository.save(registerword);
		return "redirect:/list";
	}
	
	@Transactional
	@GetMapping("/edit&move/{id}")
	public String EditMove(@PathVariable Long id, Registerword registerword, WordsILearned wordsilearned) {
	
		registerword = repository.findById(id).get();
		repository.deleteByWordIs(registerword.getWord());
		
		wordsilearned.setId(registerword.getId());
		wordsilearned.setWord(registerword.getWord());
		wordsilearned.setPartofspeech(registerword.getPartofspeech());
		wordsilearned.setMean(registerword.getMean());
		wordsilearned.setDate(registerword.getDate());
		
		repository_learned.save(wordsilearned);
		
		return "redirect:/list_learned";
	}
	
	@RequestMapping(path = "/list_learned", method = { RequestMethod.GET , RequestMethod.POST })
	public String ListLearned(SearchOption searchoption , Model model, Model model2) {
		
		
		model2.addAttribute("searchoption", searchoption);
		
		
		//sort , search and move
		String adjective = searchoption.getOption_main();
		
		
		String search_list = searchoption.getSearchword_s();
		String searchdate_list = searchoption.getSearchdate_s();
		

			
			if(searchoption.getOption_main().equals("無")) {
			
				if(searchoption.getOption_sub().equals("0")) {
					if(search_list == null && searchdate_list == null) {
						model.addAttribute("lists", repository_learned.findAll());
					}else if( search_list == null &&searchdate_list != null ){
						model.addAttribute("lists", repository_learned.findByDateLike("%"+searchdate_list+ "%"));	
					}else if(search_list != null && searchdate_list== null) {
						model.addAttribute("lists", repository_learned.findByWordLike("%"+search_list+ "%"));
					}else {
						model.addAttribute("lists", repository_learned.findByWordLikeAndDateLike("%"+search_list+ "%", "%"+searchdate_list+ "%"));
					}
					
				}
				if(searchoption.getOption_sub().equals("1")) {
					if(search_list == null && searchdate_list == null) {
						model.addAttribute("lists", repository_learned.findByOrderByWord());
					}else if( search_list == null && searchdate_list != null ){
						model.addAttribute("lists", repository_learned.findByDateLikeOrderByWord("%"+searchdate_list+ "%"));	
					}else if(search_list != null && searchdate_list== null) {
						model.addAttribute("lists", repository_learned.findByWordLikeOrderByWord("%"+search_list+ "%"));
					}else {
						model.addAttribute("lists", repository_learned.findByWordLikeAndDateLikeOrderByWord("%"+search_list+ "%", "%"+searchdate_list+ "%"));
					}
				}
				return "list_learned";
			
			}else {

				if(searchoption.getOption_sub().equals("0")) {
					if(search_list == null && searchdate_list == null) {
						model.addAttribute("lists", repository_learned.findByPartofspeechLike("%" + adjective + "%"));
					}else if(search_list != null && searchdate_list == null) {
						model.addAttribute("lists", repository_learned.findByWordLikeAndPartofspeechLike("%"+search_list+ "%", "%" + adjective + "%"));
					}else if(search_list == null && searchdate_list != null) {
						model.addAttribute("lists", repository_learned.findByDateLikeAndPartofspeechLike("%"+searchdate_list+ "%", "%" + adjective + "%"));
					}else {
						model.addAttribute("lists", repository_learned.findByDateLikeAndWordLikeAndPartofspeechLike("%"+searchdate_list+ "%", "%"+search_list+ "%" , "%" + adjective + "%"));
								}
				}else {
					if(search_list == null && searchdate_list == null) {
						model.addAttribute("lists", repository_learned.findByPartofspeechLikeOrderByWord("%" + adjective + "%"));
					}else if(search_list != null && searchdate_list == null) {
						model.addAttribute("lists", repository_learned.findByPartofspeechLikeAndWordLikeOrderByWord("%" + adjective + "%", "%"+search_list+ "%"));
					}else if(search_list == null && searchdate_list != null) {
						model.addAttribute("lists", repository_learned.findByPartofspeechLikeAndDateLikeOrderByWord("%" + adjective + "%", "%"+searchdate_list+ "%"));
					}else {
						model.addAttribute("lists", repository_learned.findByPartofspeechLikeAndDateLikeAndWordLikeOrderByWord("%" + adjective + "%", "%"+searchdate_list+ "%", "%"+search_list+ "%"));
							}
				}
				return "list_learned";
			}
			
		
		
	}
}
