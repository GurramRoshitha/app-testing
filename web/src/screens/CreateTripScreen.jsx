import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Map as MapIcon,
  ArrowLeft,
  Loader2
} from 'lucide-react';
import { generateItinerary } from '../utils/tripGenerator';
import TripService from '../data/services/TripService';
import { format, parseISO } from 'date-fns';

const CreateTripScreen = () => {
  const navigate = useNavigate();
  const [destination, setDestination] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [isGenerating, setIsGenerating] = useState(false);

  const handleGenerate = async () => {
    if (!destination || !startDate || !endDate) return;

    setIsGenerating(true);

    try {
      await new Promise(resolve => setTimeout(resolve, 1500));

      const formattedStart = format(parseISO(startDate), 'MMM dd, yyyy');
      const formattedEnd = format(parseISO(endDate), 'MMM dd, yyyy');

      const itinerary = generateItinerary(
        destination,
        formattedStart,
        formattedEnd,
        0,
        'USD'
      );

      const tripData = {
        name: destination,
        dates: `${formattedStart} - ${formattedEnd}`,
        status: "Upcoming",
        members: 1,
        total_budget: 0,
        currency: 'USD',
        travel_pace: 'Balanced',
        interests: '',
        itinerary: itinerary
      };

      const result = await TripService.createTrip(tripData);

      if (result.isSuccess) {
        navigate(`/trip-details/${result.id}`);
      }
    } catch (err) {
      console.error("Failed to generate trip:", err);
    } finally {
      setIsGenerating(false);
    }
  };

  if (isGenerating) {
    return (
      <div className="min-h-screen bg-white dark:bg-background-dark flex flex-col items-center justify-center p-8 text-center">
        <Loader2 className="w-16 h-16 text-primary animate-spin mb-8" strokeWidth={3} />
        <h2 className="text-4xl font-black text-black dark:text-white leading-tight mb-4">
          Voyage AI is analyzing <br /> local maps...
        </h2>
        <p className="text-gray-500 dark:text-gray-400 text-lg max-w-sm">
          Curating the best experiences for you in {destination}
        </p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-white dark:bg-background-dark pb-32">
      {/* Navbar */}
      <div className="px-6 py-6 flex items-center gap-4">
        <button onClick={() => navigate(-1)} className="p-2 hover:bg-gray-100 dark:hover:bg-surface-variant rounded-full transition-colors">
          <ArrowLeft size={24} />
        </button>
      </div>

      <div className="px-6">
        <h1 className="text-4xl font-black text-black dark:text-white mb-2">Plan New Journey</h1>
        <p className="text-gray-500 dark:text-gray-400 text-lg mb-10">
          AI will craft your perfect itinerary based on your destination and dates.
        </p>

        <div className="space-y-8">
          {/* Destination */}
          <section>
            <h3 className="text-xs font-bold text-secondary uppercase tracking-widest mb-3">Destination</h3>
            <div className="relative flex items-center">
              <MapIcon className="absolute left-4 text-primary pointer-events-none z-10" size={20} />
              <input
                type="text"
                value={destination}
                onChange={(e) => setDestination(e.target.value)}
                placeholder="Where to?"
                className="input-field !pl-12"
                style={{ paddingLeft: '3rem' }}
              />
            </div>
          </section>

          {/* Dates */}
          <div className="grid grid-cols-2 gap-4">
            <section>
              <h3 className="text-xs font-bold text-secondary uppercase tracking-widest mb-3">Start Date</h3>
              <div className="relative">
                <input
                  type="date"
                  value={startDate}
                  onChange={(e) => setStartDate(e.target.value)}
                  className="input-field pr-4"
                />
              </div>
            </section>
            <section>
              <h3 className="text-xs font-bold text-secondary uppercase tracking-widest mb-3">End Date</h3>
              <div className="relative">
                <input
                  type="date"
                  value={endDate}
                  onChange={(e) => setEndDate(e.target.value)}
                  className="input-field pr-4"
                />
              </div>
            </section>
          </div>
        </div>
      </div>

      {/* Bottom Bar */}
      <div className="fixed bottom-0 left-0 right-0 bg-white dark:bg-background-dark border-t border-gray-100 dark:border-surface-variant px-6 py-5">
        <button
          onClick={handleGenerate}
          disabled={!destination || !startDate || !endDate}
          className="btn-primary w-full shadow-lg shadow-primary/20"
        >
          Generate Journey
        </button>
      </div>
    </div>
  );
};

export default CreateTripScreen;
