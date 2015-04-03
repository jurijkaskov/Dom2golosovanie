package jurijkaskov.com.dom2golosovanie;

/**
 * Created by raccoon on 01.04.2015.
 */
public class Hero {
    String fio; // Анастасия Лисова
    String daysOfTheShow; // 60 дней
    String startDate; // c 31 января
    String ageHero; // 24 года
    String city; // Москва
    String signOfTheZodiac; // знак зодиака
    String description; // Описание героя
    String photo; // Фото
    String heroId; // id на сайте

    public String getHeroId() {
        return heroId;
    }

    public void setHeroId(String heroId) {
        this.heroId = heroId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getDaysOfTheShow() {
        return daysOfTheShow;
    }

    public void setDaysOfTheShow(String daysOfTheShow) {
        this.daysOfTheShow = daysOfTheShow;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getAgeHero() {
        return ageHero;
    }

    public void setAgeHero(String ageHero) {
        this.ageHero = ageHero;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSignOfTheZodiac() {
        return signOfTheZodiac;
    }

    public void setSignOfTheZodiac(String signOfTheZodiac) {
        this.signOfTheZodiac = signOfTheZodiac;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
