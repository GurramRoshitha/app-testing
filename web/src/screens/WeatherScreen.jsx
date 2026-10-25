import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Sun,
  Cloud,
  CloudRain,
  CloudLightning,
  Droplets,
  Wind,
  Loader2
} from 'lucide-react';
import WeatherService from '../data/services/WeatherService';

const WeatherScreen = () => {
  const { destination } = useParams();
  const navigate = useNavigate();
  const [weather, setWeather] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchWeather = async () => {
      setLoading(true);
      const data = await WeatherService.getWeather(destination === 'current' ? 'Chennai' : destination);
      setWeather(data);
      setLoading(false);
    };
    fetchWeather();
  }, [destination]);

  const getWeatherIcon = (condition, size = 24, className = "") => {
    if (!condition) return <Sun size={size} className={className} />;
    const cond = condition.toLowerCase();
    if (cond.includes('sun') || cond.includes('clear')) return <Sun size={size} className={className} />;
    if (cond.includes('cloud')) return <Cloud size={size} className={className} />;
    if (cond.includes('rain') || cond.includes('drizzle')) return <CloudRain size={size} className={className} />;
    if (cond.includes('thunderstorm')) return <CloudLightning size={size} className={className} />;
    return <Cloud size={size} className={className} />;
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-white dark:bg-background-dark">
        <Loader2 className="w-12 h-12 text-primary animate-spin" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-white dark:bg-background-dark px-6 pb-12">
      <div className="max-w-2xl mx-auto">
        <header className="py-8 flex items-center gap-4">
          <button onClick={() => navigate(-1)} className="p-2 hover:bg-gray-100 dark:hover:bg-surface-variant rounded-full transition-colors">
            <ArrowLeft size={24} />
          </button>
        </header>

        <h1 className="text-5xl font-black text-primary mb-2">Sky Forecast</h1>
        <p className="text-gray-500 dark:text-gray-400 text-lg mb-10 capitalize">
          Weather for {destination === 'current' ? 'Current Location' : destination}
        </p>

        {weather ? (
          <>
            {/* Main Weather Card */}
            <div className="rounded-[32px] bg-gradient-to-b from-primary to-secondary p-8 text-white shadow-xl shadow-primary/20 mb-12 text-center">
              <div className="flex justify-center mb-6">
                {getWeatherIcon(weather.condition, 80, "text-white")}
              </div>
              <div className="text-7xl font-black mb-2 tracking-tighter">
                {Math.round(weather.temp)}°C
              </div>
              <div className="text-3xl font-bold opacity-90 mb-6">
                {weather.condition}
              </div>

              <div className="flex justify-center items-center gap-6 py-4 px-6 bg-white/10 backdrop-blur-md rounded-2xl w-fit mx-auto text-sm font-bold">
                <div className="flex items-center gap-2">
                  <Droplets size={16} />
                  <span>Humidity: {weather.humidity}%</span>
                </div>
                <div className="w-px h-4 bg-white/20"></div>
                <div className="flex items-center gap-2">
                  <Wind size={16} />
                  <span>Wind: {weather.windSpeed} km/h</span>
                </div>
              </div>
            </div>

            {/* 7-Day Outlook */}
            <h2 className="text-3xl font-black text-primary font-serif mb-6">7-Day Outlook</h2>
            <div className="flex gap-4 overflow-x-auto pb-6 no-scrollbar">
              {weather.dailyForecast.map((day, idx) => (
                <div key={idx} className="min-w-[100px] card p-5 flex flex-col items-center gap-4 text-center">
                  <span className="text-xs font-bold text-gray-500 uppercase tracking-widest">{day.day}</span>
                  <div className="text-primary">
                    {getWeatherIcon(day.condition, 32)}
                  </div>
                  <span className="text-lg font-black text-primary">{Math.round(day.temp)}°C</span>
                </div>
              ))}
            </div>
          </>
        ) : (
          <div className="text-center py-20 card">
            <p className="text-gray-500">Weather data unavailable</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default WeatherScreen;
