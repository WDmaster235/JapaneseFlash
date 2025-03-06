package com.example.japaneseflash;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Kanji {
    @SerializedName("kanji")
    private String character;

    @SerializedName("kun_readings")
    private List<String> kunReadings;

    @SerializedName("on_readings")
    private List<String> onReadings;

    @SerializedName("meanings")
    private List<String> meanings;

    @SerializedName("grade")
    private Integer grade;

    @SerializedName("jlpt")
    private Integer jlpt;

    @SerializedName("stroke_count")
    private Integer strokeCount;

    // Getters and Setters
    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public List<String> getKunReadings() {
        return kunReadings;
    }

    public void setKunReadings(List<String> kunReadings) {
        this.kunReadings = kunReadings;
    }
    private List<String> hiraganaReadings;  // New field for Hiragana readings
    private List<String> katakanaReadings;  // New field for Katakana readings

    // Getters and Setters for new fields
    public List<String> getHiraganaReadings() {
        return hiraganaReadings;
    }

    public void setHiraganaReadings(List<String> hiraganaReadings) {
        this.hiraganaReadings = hiraganaReadings;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Kanji kanji = (Kanji) obj;
        return character.equals(kanji.character); // Compare Kanji characters
    }

    @Override
    public int hashCode() {
        return character.hashCode(); // Hash based on Kanji character
    }

    public List<String> getKatakanaReadings() {
        return katakanaReadings;
    }

    public void setKatakanaReadings(List<String> katakanaReadings) {
        this.katakanaReadings = katakanaReadings;
    }
    public List<String> getOnReadings() {
        return onReadings;
    }

    public void setOnReadings(List<String> onReadings) {
        this.onReadings = onReadings;
    }

    public List<String> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<String> meanings) {
        this.meanings = meanings;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getJlpt() {
        return jlpt;
    }

    public void setJlpt(Integer jlpt) {
        this.jlpt = jlpt;
    }

    public Integer getStrokeCount() {
        return strokeCount;
    }

    public void setStrokeCount(Integer strokeCount) {
        this.strokeCount = strokeCount;
    }
}
