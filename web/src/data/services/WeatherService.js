import axios from 'axios';

class WeatherService {
  async getWeather(cityName) {
    try {
      // 1. Get Coordinates using Nominatim
      const geoResponse = await axios.get(
        `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(cityName)}&format=json&limit=1`,
        { headers: { 'User-Agent': 'VoyageTravelPlanner/1.0' } }
      );

      if (geoResponse.data.length === 0) return null;

      const { lat, lon } = geoResponse.data[0];

      // 2. Get Weather using Open-Meteo
      const weatherResponse = await axios.get(
        `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code&daily=weather_code,temperature_2m_max&timezone=auto`
      );

      const current = weatherResponse.data.current;
      const daily = weatherResponse.data.daily;

      const forecasts = daily.time.map((time, i) => ({
        day: new Date(time).toLocaleDateString('en-US', { weekday: 'short' }),
        temp: daily.temperature_2m_max[i],
        condition: this.getWeatherCondition(daily.weather_code[i])
      }));

      return {
        temp: current.temperature_2m,
        condition: this.getWeatherCondition(current.weather_code),
        humidity: current.relative_humidity_2m,
        windSpeed: current.wind_speed_10m,
        dailyForecast: forecasts
      };
    } catch (error) {
      console.error("Error fetching weather:", error);
      return null;
    }
  }

  getWeatherCondition(code) {
    if (code === 0) return "Clear Sky";
    if ([1, 2, 3].includes(code)) return "Partly Cloudy";
    if ([45, 48].includes(code)) return "Foggy";
    if ([51, 53, 55].includes(code)) return "Drizzle";
    if ([61, 63, 65].includes(code)) return "Rainy";
    if ([71, 73, 75].includes(code)) return "Snowy";
    if ([95, 96, 99].includes(code)) return "Thunderstorm";
    return "Cloudy";
  }
}

export default new WeatherService();
