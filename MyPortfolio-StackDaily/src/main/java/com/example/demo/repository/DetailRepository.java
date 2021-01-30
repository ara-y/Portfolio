package com.example.demo.repository;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDate;

import com.example.demo.model.Detail;
import java.util.List;
import java.util.Optional;
public interface DetailRepository extends CrudRepository<Detail, Integer> {
	//o 一番最後の日付のデータを取得する
	List<Detail> findFirstByContributorOrderByDoneDateDesc(String loginUser);
	
	//o 指定した日付のデータを取得
	List<Detail> findByDoneDateAndContributor(LocalDate date,String loginUser);
	//o　投稿者のデータの数を数える
	int countByContributor(String loginUser);
	//o　ある期間のデータを取得
	List<Detail> findByContributorAndDoneDateBetween(String loginuser, LocalDate firstDay, LocalDate lastDay);
	//o指定した日時のデータを消す
	List<Detail> deleteFirstByDoneDateAndContributorAndWhatIDid(LocalDate date, String loginUser, String whatIDid);
	//o指定した日時の一つのタスクを取得するための検索
	List<Detail> findByDoneDateAndContributorAndWhatIDid(LocalDate date,String loginUser,String whatIDid);
	
}
