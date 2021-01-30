package com.example.demo.model;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class Form {
	//createにおける翌日、昨日の設定
	private boolean nextDate;
	private boolean backDate;
	
	//homeにおける表示する月の設定
	private LocalDate specifyAnotherMonth = LocalDate.of(2019, 01, 01);
	private int specifyYear = 2021;
	private int specifyMonth = 01;
	
	//createにおいて誤って投稿した時の削除の設定
	private LocalDate nowDate;
	private String deleteWhatIDid;
	
	//detailにおいて詳細の日付を格納する。itemにも使う。
	private LocalDate detailDate;
	
	//detail postにおいてリダイレクト先の日付を指定する
	private int detailDay;
	
	//detail/taskにおいて、該当のレコードにコメントを追加するためのタスクの指定
	private String taskData;
}
