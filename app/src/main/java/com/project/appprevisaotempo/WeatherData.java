package com.project.appprevisaotempo;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {

    private String temperatura, iconClima, cidade, tipoClima, sensacaoTermica, umidade, vento, pressao;
    private int condicao;

    public static WeatherData fromJson(JSONObject jsonObject) {

        try {

            WeatherData weatherData = new WeatherData();
            weatherData.cidade = jsonObject.getString("name");
            weatherData.condicao = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.tipoClima = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            weatherData.iconClima = atualizarIconClima(weatherData.condicao);
            double tempResult = jsonObject.getJSONObject("main").getDouble("temp")-273.15;
            double sensacaoTermicaResult = jsonObject.getJSONObject("main").getDouble("feels_like") - 273.15;
            double umidadeResult = jsonObject.getJSONObject("main").getDouble("humidity");
            double ventoResult = jsonObject.getJSONObject("wind").getDouble("speed") * 3.6;
            double pressaoResult = jsonObject.getJSONObject("main").getDouble("pressure");

            int UmidadeFormatado = (int)Math.rint(umidadeResult);
            weatherData.umidade = Integer.toString(UmidadeFormatado);

            int sensacaoTermFormatado = (int)Math.rint(sensacaoTermicaResult);
            weatherData.sensacaoTermica = Integer.toString(sensacaoTermFormatado);

            int temperaturaFormatado = (int)Math.rint(tempResult);
            weatherData.temperatura = Integer.toString(temperaturaFormatado);

            int pressaoFormatado = (int)Math.rint(pressaoResult);
            weatherData.pressao = Integer.toString(pressaoFormatado);

            int ventoFormatado = (int)Math.rint(ventoResult);
            weatherData.vento = Integer.toString(ventoFormatado);

            return weatherData;


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String atualizarIconClima(int condicao) {
        if(condicao>=0 && condicao<=300)
        {
            return "thunderstrom1";
        }
        else if(condicao>=300 && condicao<=500)
        {
            return "lightrain";
        }
        else if(condicao>=500 && condicao<=600)
        {
            return "shower";
        }
        else  if(condicao>=600 && condicao<=700)
        {
            return "snow";
        }
        else if(condicao>=701 && condicao<=771)
        {
            return "fog";
        }

        else if(condicao>=772 && condicao<=800)
        {
            return "overcast";
        }
        else if(condicao==800)
        {
            return "sunny";
        }
        else if(condicao>=801 && condicao<=804)
        {
            return "cloudy";
        }
        else  if(condicao>=900 && condicao<=902)
        {
            return "thunderstrom1";
        }
        if(condicao==903)
        {
            return "snow";
        }
        if(condicao==904)
        {
            return "sunny";
        }
        if(condicao>=905 && condicao<=1000)
        {
            return "thunderstrom2";
        }

        return "dunno";
    }

    public String getTemperatura() {
        return temperatura+"º";
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getIconClima() {
        return iconClima;
    }

    public void setIconClima(String iconClima) {
        this.iconClima = iconClima;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getTipoClima() {
        return tipoClima;
    }

    public void setTipoClima(String tipoClima) {
        this.tipoClima = tipoClima;
    }

    public String getSensacaoTermica() {
        return sensacaoTermica + " ºC";
    }

    public void setSensacaoTermica(String sensacaoTermica) {
        this.sensacaoTermica = sensacaoTermica;
    }

    public String getUmidade() {
        return umidade + " %";
    }

    public void setUmidade(String umidade) {
        this.umidade = umidade;
    }

    public String getVento() {
        return vento + " Km/h";
    }

    public void setVento(String vento) {
        this.vento = vento;
    }

    public String getPressao() {
        return pressao + " hPa";
    }

    public void setPressao(String pressao) {
        this.pressao = pressao;
    }
}
