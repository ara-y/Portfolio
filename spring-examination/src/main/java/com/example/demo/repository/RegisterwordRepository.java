package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Registerword;
import java.util.Collection;
import java.util.List;

public interface RegisterwordRepository extends JpaRepository<Registerword, Long> {
	Collection<Registerword>findByWordIs(String word);
	
	Collection<Registerword>findByOrderByWord();
	
	Collection<Registerword>findByPartofspeechLike(String partofspeech);
	
	Collection<Registerword>findByPartofspeechLikeOrderByWord(String partofspeech);
	
	Collection<Registerword>findByWordLike(String word);
	
	Collection<Registerword>findByMeanIs(String mean);
	
	Collection<Registerword>deleteByWordIs(String word);
	
	Collection<Registerword>findByDateLike(String date);
	
	List<Registerword>findByDateGreaterThan(String date);
	
	Collection<Registerword>findByWordLikeAndDateLike(String word, String date);
	
	Collection<Registerword>findByWordLikeAndPartofspeechLike(String word, String partofspeech);
	
	Collection<Registerword>findByDateLikeAndPartofspeechLike(String date, String partofspeech);
	
	Collection<Registerword>findByDateLikeAndWordLikeAndPartofspeechLike(String date, String word ,String partofspeech);
	
	Collection<Registerword>findByPartofspeechLikeAndWordLikeOrderByWord(String partofspeech, String word);
	
	Collection<Registerword>findByPartofspeechLikeAndDateLikeOrderByWord(String partofspeech, String date);
	
	Collection<Registerword>findByPartofspeechLikeAndDateLikeAndWordLikeOrderByWord(String partofspeech, String date, String word);
	
	Collection<Registerword>findByWordLikeOrderByWord(String word);
	
	Collection<Registerword>findByDateLikeOrderByWord(String date);
	
	
	
	Collection<Registerword>findByWordLikeAndDateLikeOrderByWord(String word, String date);
}
