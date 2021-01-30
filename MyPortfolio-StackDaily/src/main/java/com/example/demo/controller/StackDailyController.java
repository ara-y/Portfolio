package com.example.demo.controller;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Contents;
import com.example.demo.model.Detail;
import com.example.demo.model.Form;
import com.example.demo.model.SiteUser;
import com.example.demo.repository.ContentsRepository;
import com.example.demo.repository.DetailRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.Role;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
@SessionAttributes(types = {Form.class})
@Scope("prototype")
public class StackDailyController {
	private final UserRepository userRepository;
	private final ContentsRepository contentsRepository;
	private final DetailRepository detailRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
	@ModelAttribute("form")
	private Form keepForm(Form form) {
		return form;
	}
	
	
	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("isError", false);
		return "login";
	}
	
	@GetMapping("/loginError")
	public String loginError(Model model) {
		model.addAttribute("isError", true);
		return "login";
	}
	
	//home画面　ユーザの記録が表示される。
	@GetMapping("/home")
	public String displayHome(Authentication loginUser, Model model, @ModelAttribute Form form) {
		model.addAttribute("name", loginUser.getName());
		//o 月の表示。
		int thisMonth = LocalDate.now().getMonthValue();
		model.addAttribute("thisMonth", thisMonth);
		
		if(form.getSpecifyAnotherMonth().isEqual(LocalDate.of(2019, 01, 01))) {
		//monthの日数を入手する。月の初め、終わりの日も取得する
			int daysOfThisMonth = LocalDate.now().lengthOfMonth();
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
			DateTimeFormatter fmtd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String convertFirstDay = LocalDate.now().format(fmt)+"-01";
			LocalDate first = LocalDate.parse(convertFirstDay, fmtd);
		
			String convertLastDay = LocalDate.now().format(fmt)+ "-"+daysOfThisMonth;
			LocalDate last = LocalDate.parse(convertLastDay, fmtd);
		//1か月分のdataを取得する
			List<Detail> data = detailRepository.findByContributorAndDoneDateBetween(loginUser.getName(), first, last);
		
		//o タスクを日ごとに追加する
			if(!(CollectionUtils.isEmpty(data))) {
			for(int i=0; i<daysOfThisMonth - data.get(0).getDoneDate().getDayOfMonth(); i++) {
				int DayOfEachTask = i + data.get(0).getDoneDate().getDayOfMonth();
			
				List<Detail> TasksOfTheDay = new ArrayList<Detail>();
			
			for(int j=0; j<data.size(); j++) {
				if(DayOfEachTask == data.get(j).getDoneDate().getDayOfMonth()) {
					TasksOfTheDay.add(data.get(j));
				}
			}
				model.addAttribute("task" + DayOfEachTask, TasksOfTheDay);
			}
		
		//o　detail設定のために月と年の確保
			form.setSpecifyYear(LocalDate.now().getYear());
			form.setSpecifyMonth(LocalDate.now().getMonthValue());
			}else {
				return "redirect:/TaskSetting";
			}
		}else {
		//o 過去の月や将来の月の情報を閲覧できるようにする specifyAnotherMonthは2021-1-1など月始めに設定
			
			LocalDate first = form.getSpecifyAnotherMonth();
			
			int daysOfThisMonth = form.getSpecifyAnotherMonth().lengthOfMonth();
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
			DateTimeFormatter fmtd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			
			String convertLastDay = form.getSpecifyAnotherMonth().format(fmt)+ "-"+daysOfThisMonth;
			LocalDate last = LocalDate.parse(convertLastDay, fmtd);
			
			//o　タスクが存在するかのチェック。ない場合はセッション情報の日時をリセット
			if(!(detailRepository.findByContributorAndDoneDateBetween(loginUser.getName(),first, last).isEmpty())) {
			List<Detail> data = detailRepository.findByContributorAndDoneDateBetween(loginUser.getName(),first, last);
			
			//o タスクを日ごとに追加する
				for(int i=0; i<daysOfThisMonth - data.get(0).getDoneDate().getDayOfMonth(); i++) {
					int DayOfEachTask = i + data.get(0).getDoneDate().getDayOfMonth();
				
					List<Detail> TasksOfTheDay = new ArrayList<Detail>();
				
				for(int j=0; j<data.size(); j++) {
					if(DayOfEachTask == data.get(j).getDoneDate().getDayOfMonth()) {
						TasksOfTheDay.add(data.get(j));
					}
				}
					model.addAttribute("task" + DayOfEachTask, TasksOfTheDay);
			}
			}else {
				form.setSpecifyAnotherMonth(LocalDate.of(2019, 01, 01));
				return "redirect:/home";
			}
		}
		
		return "Helloworld";
	}

	@PostMapping("/home")
	public String changeDisplay(@ModelAttribute Form form) {
		
		form.setSpecifyAnotherMonth(LocalDate.of(form.getSpecifyYear(), form.getSpecifyMonth(), 1));
		
		return "redirect:/home";
	}
	
	//register アカウントの登録画面
	@GetMapping("/register")
	public String register(@ModelAttribute SiteUser user) {
		return"register";
	}
	
	@PostMapping("/register")
	public String process(@Validated @ModelAttribute SiteUser user, BindingResult result) {
		if(result.hasErrors()) {
			return "register";
		}
		if(userRepository.existsByName(user.getName())) {
			System.out.println("登録に失敗しましたとエラーメッセージを表示したい");
			return "register";
		}
		System.out.println(user.getName());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if(user.isAdmin()) {
			user.setRole(Role.ADMIN.name());
		}else {
			user.setRole(Role.USER.name());
		}
		userRepository.save(user);
		
		return "redirect:/login?register";
	}
	
	
	
	//TaskSetting　スケジュールの新規作成（最大30日）
	@GetMapping("/TaskSetting")
	public String TaskSetting(@ModelAttribute Detail detail, Authentication loginUser, Model model, @ModelAttribute Form form, SessionStatus status) {
		//model.addAttribute("d", detailRepository.findFirstByContributorOrderByDoneDateDesc(loginUser.getName()));
		
		//Task投稿者が初めての時、または最期に登録したTaskが現在よりも過去の場合
		if(detailRepository.countByContributor(loginUser.getName()) == 0 || detailRepository.findFirstByContributorOrderByDoneDateDesc(loginUser.getName()).get(0).getDoneDate().isBefore(LocalDate.now())) {
			//Taskがない時に新規に作成する。
			detail.setDoneDate(LocalDate.now().plusDays(1));
			detail.setContributor(loginUser.getName());
			model.addAttribute("d", detailRepository.findByDoneDateAndContributor(detail.getDoneDate(), loginUser.getName()));
			form.setNowDate(detail.getDoneDate());
			}else {
			//dateを進めて投稿するか、連続で投稿するかの設定
			//dateChangeの設定に合わせた日時を作成
				List<Detail> lastTask;
			
			lastTask = detailRepository.findFirstByContributorOrderByDoneDateDesc(loginUser.getName());
			
			
			if(lastTask != null) {
			
			LocalDate nextTask;
			if(form.isNextDate() == true) {
			//o 翌日のTaskを設定する
			 nextTask = lastTask.get(0).getDoneDate().plusDays(1);
			 if(form.getNowDate() != null) {
				 nextTask = form.getNowDate().plusDays(1);
			 }
			}else {
			//o 当日に追加する
			 nextTask = lastTask.get(0).getDoneDate();
			}
			
			//o task表示を一日戻す
			if(form.isBackDate() == true) {
			nextTask = form.getNowDate().minusDays(1);
				
				form.setBackDate(false);
			}
			
			if(form.isNextDate() == true) {
				form.setNextDate(false);
				
			}
			
			//nextTaskは最大28日分作成できる
			if(nextTask.isBefore(LocalDate.now().plusDays(27))) {
				detail.setDoneDate(nextTask);
				detail.setContributor(loginUser.getName());
				form.setNowDate(nextTask);
			}else {
				System.out.println("これ以上作成できませんとエラーメッセージを表示したい");
				return "CreateHelloWorld";
			}
			
			model.addAttribute("d", detailRepository.findByDoneDateAndContributor(nextTask, loginUser.getName()));
			//o　backDateするときの基準となる日を設定
			form.setNowDate(nextTask);
			
			}
			}
			LocalDate displayThisDay = detail.getDoneDate();
			model.addAttribute("displayThisDay", displayThisDay);
			
		return "CreateHelloWorld";
	}
	//o 送信後に一日増えないようにする。ボタンによる値送信で次に進むようにする
	@PostMapping("/TaskSetting/post")
	public String PostTask(@Validated @ModelAttribute Detail detail, BindingResult result, Authentication loginUser, Model model, @ModelAttribute Form form) {
		
		//o hiddenを使用しているため、チェックを行う
		if(detail.getWhatIDid() != null) {
			if(result.hasErrors()) {
					return "TaskSetting";
				}
		//o 投稿者とログインユーザが一致しているかの検証
			if(detail.getContributor().equals(loginUser.getName())) {
			detailRepository.save(detail);
				}
		}
		return "redirect:/TaskSetting";
	}
	
	@Transactional
	@PostMapping("TaskSetting/delete")
	public String deleteTask(@ModelAttribute Detail detail, @ModelAttribute Form form, Authentication loginUser) {
		System.out.println(form.getNowDate());
		
			String deleteTask = form.getDeleteWhatIDid();
			detailRepository.deleteFirstByDoneDateAndContributorAndWhatIDid(form.getNowDate(), loginUser.getName(), deleteTask);
		
		return "redirect:/TaskSetting";
	}
	
	
	
	//o 詳細設定
	@GetMapping("/home/detail/{day}")
	public String taskList(@ModelAttribute Form form ,@ModelAttribute Detail detail, @ModelAttribute Contents contents, Authentication loginUser, Model model, @PathVariable("day")int day) {
		
		form.setDetailDate(LocalDate.of(form.getSpecifyYear(), form.getSpecifyMonth(), day));
	
		//o 日付、ユーザに一致したdetailのwhatIDid項目を取得する。
		model.addAttribute("task", detailRepository.findByDoneDateAndContributor(form.getDetailDate(), loginUser.getName()));
		
		//o 日記記入欄を導入したいので型をセットしていく。日記自体はポストから入力する。
		contents.setContributor(loginUser.getName());
		contents.setDdate(form.getDetailDate());
		
		//postから戻ってくるための値を設定
		form.setDetailDay(day);
		
		//o　書き直したいときに合わせてdairyがnullではない時の処理も加える
		
		Collection<Contents> openDairy = contentsRepository.findByContributorAndDdate(loginUser.getName(), form.getDetailDate());
		
			if(openDairy.size()!=0) {
			List<Contents> getDairy =  (List<Contents>)openDairy;
			model.addAttribute("display", getDairy.get(0).getDairy());
			}
		
		
		return "HelloWorld1";
	}
	
	@Transactional
	@PostMapping("/home/detail/post")
	public String DairyPost(@ModelAttribute Detail detail, @Validated @ModelAttribute Contents contents, BindingResult result,RedirectAttributes redirectattributes, @ModelAttribute Form form, Authentication loginUser) {
		
		if(result.hasErrors()) {
			System.out.println("ko");
			return "HelloWorld1";
		}
		
		contents.setContributor(loginUser.getName());
		contents.setDdate(form.getDetailDate());
		
		//contentsのこの日の投稿がある場合は古い情報を削除し、新たに投稿しなおす
		if(contentsRepository.findByContributorAndDdate(loginUser.getName(), form.getDetailDate())==null) {
			contentsRepository.save(contents);
		}else {
			contentsRepository.deleteByContributorAndDdate(loginUser.getName(), form.getDetailDate());
			contentsRepository.save(contents);
		}
		//o リダイレクト先を指定
		redirectattributes.addAttribute("day", form.getDetailDay());
		return "redirect:/home/detail/{day}";
	}
	
	@GetMapping("/home/detail/task/{thisTask}/{day}")
	public String TaskDairy(@PathVariable("thisTask") String thisTask ,@PathVariable("day") String day, @ModelAttribute Form form, Model model, Authentication loginUser, @ModelAttribute Detail detail) {
		
		
		form.setDetailDate(LocalDate.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		form.setTaskData(thisTask);
		
		//commentがすでにある場合は表示する
		List<Detail> commentExist = detailRepository.findByDoneDateAndContributorAndWhatIDid(form.getDetailDate(), loginUser.getName(), form.getTaskData());
		if(commentExist.get(0).getComment()!=null) {
			model.addAttribute("display",commentExist.get(0).getComment());
		}
		return "HelloWorld2";
	}
	
	@Transactional
	@PostMapping("/home/detail/task/post")
	public String PostTaskDairy(@ModelAttribute Detail detail,@ModelAttribute Form form,Authentication loginUser,RedirectAttributes redirectAttributes) {
		
		detailRepository.deleteFirstByDoneDateAndContributorAndWhatIDid(form.getDetailDate(), loginUser.getName(), form.getTaskData());
		
		detail.setWhatIDid(form.getTaskData());
		detail.setDoneDate(form.getDetailDate());
		detail.setContributor(loginUser.getName());
		detailRepository.save(detail);
		
		//o LocalDateをStringに変形し、リダイレクトに埋め込む
		String day = form.getDetailDate().toString();
		
		redirectAttributes.addAttribute("thisTask", form.getTaskData());
		redirectAttributes.addAttribute("day", day);
		return "redirect:/home/detail/task/{thisTask}/{day}";
	}
}
