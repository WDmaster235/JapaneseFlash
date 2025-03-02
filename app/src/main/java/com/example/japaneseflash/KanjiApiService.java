package com.example.japaneseflash;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface KanjiApiService {

    // Endpoint to get Jōyō Kanji list
    @GET("v1/kanji/joyo")
    Call<List<String>> getJoyoKanji();

    // Endpoint to get Jinmeiyō Kanji list
    @GET("v1/kanji/jinmeiyo")
    Call<List<String>> getJinmeiyoKanji();

    // Endpoint to get Kyōiku Kanji list
    @GET("v1/kanji/kyouiku")
    Call<List<String>> getKyouikuKanji();

    // Endpoints for readings
    @GET("v1/kanji/{character}/hiragana")
    Call<List<String>> getHiraganaReadings(@Path("character") String character);

    @GET("v1/kanji/{character}/katakana")
    Call<List<String>> getKatakanaReadings(@Path("character") String character);

    // Endpoint to get Kanji details by character
    @GET("v1/kanji/{character}")
    Call<Kanji> getKanjiDetails(@Path("character") String character);
}
