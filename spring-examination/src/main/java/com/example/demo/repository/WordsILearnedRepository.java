package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;

import com.example.demo.model.WordsILearned;

public interface WordsILearnedRepository extends JpaRepository<WordsILearned, Long> {
	Collection<WordsILearned>findByWordIs(String word);
	
	Collection<WordsILearned>findByOrderByWord();
	
	Collection<WordsILearned>findByPartofspeechLike(String partofspeech);
	
	Collection<WordsILearned>findByPartofspeechLikeOrderByWord(String partofspeech);
	
	Collection<WordsILearned>findByWordLike(String word);
	
	Collection<WordsILearned>findByMeanIs(String mean);
	
	Collection<WordsILearned>deleteByWordIs(String word);
	
	Collection<WordsILearned>findByDateLike(String date);
	
	List<WordsILearned>findByDateGreaterThan(String date);
	
	Collection<WordsILearned>findByWordLikeAndDateLike(String word, String date);
	
	Collection<WordsILearned>findByWordLikeAndPartofspeechLike(String word, String partofspeech);
	
	Collection<WordsILearned>findByDateLikeAndPartofspeechLike(String date, String partofspeech);
	
	Collection<WordsILearned>findByDateLikeAndWordLikeAndPartofspeechLike(String date, String word ,String partofspeech);
	
	Collection<WordsILearned>findByPartofspeechLikeAndWordLikeOrderByWord(String partofspeech, String word);
	
	Collection<WordsILearned>findByPartofspeechLikeAndDateLikeOrderByWord(String partofspeech, String date);
	
	Collection<WordsILearned>findByPartofspeechLikeAndDateLikeAndWordLikeOrderByWord(String partofspeech, String date, String word);
	
	Collection<WordsILearned>findByWordLikeOrderByWord(String word);
	
	Collection<WordsILearned>findByDateLikeOrderByWord(String date);
	
	
	
	Collection<WordsILearned>findByWordLikeAndDateLikeOrderByWord(String word, String date);
}
