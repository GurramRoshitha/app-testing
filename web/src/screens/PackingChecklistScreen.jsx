import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Plus,
  RotateCcw,
  Trash2,
  Check,
  Luggage,
  Sparkles,
  X,
  CloudSun
} from 'lucide-react';
import TripService from '../data/services/TripService';
import WeatherService from '../data/services/WeatherService';
import { generateRecommendations } from '../utils/packingRecommendationEngine';

const PackingChecklistScreen = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [trip, setTrip] = useState(null);
  const [loading, setLoading] = useState(true);
  const [items, setItems] = useState([]);
  const [summaryText, setSummaryText] = useState('Analyzing trip details...');
  const [weatherInfo, setWeatherInfo] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);

  // Custom Item Form States
  const [customName, setCustomName] = useState('');
  const [customQuantity, setCustomQuantity] = useState('1');
  const [customCategory, setCustomCategory] = useState('Travel Essentials');

  const categories = [
    "Clothing",
    "Travel Essentials",
    "Toiletries",
    "Weather Essentials",
    "Activity-Specific Items",
    "Documents and Safety"
  ];

  const storageKey = `voyage_packing_${id}`;

  const saveChecklist = (newItems) => {
    setItems(newItems);
    localStorage.setItem(storageKey, JSON.stringify(newItems));
  };

  const runEngineAndApply = (currentTrip, currentItems, weather) => {
    const result = generateRecommendations(currentTrip, weather, currentItems);
    setSummaryText(result.factorsSummary);
    saveChecklist(result.items);
  };

  useEffect(() => {
    const fetchTrip = async () => {
      setLoading(true);
      const result = await TripService.getTripById(id);
      if (result.isSuccess) {
        setTrip(result.trip);
        const saved = JSON.parse(localStorage.getItem(storageKey) || '[]');

        // Try fetching live weather
        let weather = null;
        try {
          weather = await WeatherService.getWeather(result.trip.name);
          setWeatherInfo(weather);
        } catch (e) {}

        if (saved.length > 0) {
          setItems(saved);
          const rec = generateRecommendations(result.trip, weather, saved);
          setSummaryText(rec.factorsSummary);
        } else {
          runEngineAndApply(result.trip, [], weather);
        }
      } else {
        navigate('/dashboard');
      }
      setLoading(false);
    };
    fetchTrip();
  }, [id, navigate, storageKey]);

  const handleToggle = (itemId) => {
    const updated = items.map(item =>
      item.id === itemId ? { ...item, isPacked: !item.isPacked } : item
    );
    saveChecklist(updated);
  };

  const handleDelete = (itemId) => {
    const updated = items.filter(item => item.id !== itemId);
    saveChecklist(updated);
  };

  const handleAddCustom = (e) => {
    e.preventDefault();
    if (!customName.trim()) return;

    const newItem = {
      id: `custom_${Date.now()}`,
      name: customName.trim(),
      category: customCategory,
      quantity: customQuantity.trim() || '1',
      isPacked: false,
      isCustom: true
    };

    const updated = [...items, newItem];
    saveChecklist(updated);

    setCustomName('');
    setCustomQuantity('1');
    setShowAddModal(false);
  };

  const handleRegenerate = () => {
    if (!trip) return;
    runEngineAndApply(trip, items, weatherInfo);
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-white dark:bg-background-dark">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
    );
  }

  if (!trip) return null;

  const totalCount = items.length;
  const packedCount = items.filter(i => i.isPacked).length;
  const percentage = totalCount > 0 ? Math.round((packedCount / totalCount) * 100) : 0;

  return (
    <div className="min-h-screen bg-white dark:bg-background-dark pb-24">
      {/* Top Bar */}
      <div className="px-6 py-4 flex items-center justify-between sticky top-0 bg-white/80 dark:bg-background-dark/80 backdrop-blur-md z-40 border-b border-gray-100 dark:border-surface-variant">
        <div className="flex items-center gap-4">
          <button onClick={() => navigate(-1)} className="p-2 hover:bg-gray-100 dark:hover:bg-surface-variant rounded-full transition-colors">
            <ArrowLeft size={24} />
          </button>
          <div>
            <h1 className="text-xl font-black text-black dark:text-white leading-tight">{trip.name}</h1>
            <p className="text-xs text-gray-500 font-medium">{trip.dates}</p>
          </div>
        </div>

        <button
          onClick={handleRegenerate}
          title="Regenerate Suggestions"
          className="p-2 text-primary hover:bg-primary/5 rounded-full transition-colors flex items-center gap-1 font-bold text-xs"
        >
          <RotateCcw size={20} />
        </button>
      </div>

      <div className="px-6 py-6 max-w-2xl mx-auto space-y-6">
        {/* Analysis Factors Summary Box */}
        <div className="card p-4 bg-gray-50 dark:bg-surface-variant/30 border border-primary/20 flex items-center gap-3">
          <Sparkles size={22} className="text-primary shrink-0" />
          <p className="text-xs font-semibold text-black dark:text-white leading-relaxed">
            {summaryText}
          </p>
        </div>

        {/* Progress Card */}
        <div className="card p-6 bg-gradient-to-br from-primary/5 to-secondary/5 border border-primary/10">
          <div className="flex justify-between items-center mb-3">
            <div>
              <span className="text-[10px] font-black tracking-widest text-primary uppercase block mb-1">
                PACKING PROGRESS
              </span>
              <h3 className="text-2xl font-black text-black dark:text-white">
                {packedCount} of {totalCount} items packed
              </h3>
            </div>
            <div className="w-12 h-12 rounded-2xl bg-primary text-white font-black flex items-center justify-center text-sm shadow-lg shadow-primary/20">
              {percentage}%
            </div>
          </div>

          <div className="w-full bg-primary/10 h-3 rounded-full overflow-hidden">
            <div
              className="bg-primary h-full transition-all duration-500 rounded-full"
              style={{ width: `${percentage}%` }}
            ></div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex gap-3">
          <button
            onClick={() => setShowAddModal(true)}
            className="btn-primary flex-1 py-3 text-xs flex items-center justify-center gap-2 shadow-md shadow-primary/20"
          >
            <Plus size={16} />
            <span>Add Custom Item</span>
          </button>

          <button
            onClick={handleRegenerate}
            className="btn-secondary flex-1 py-3 text-xs flex items-center justify-center gap-2 shadow-md"
          >
            <Sparkles size={16} />
            <span>Regenerate Suggestions</span>
          </button>
        </div>

        {/* Categories & Items List */}
        {totalCount === 0 ? (
          <div className="text-center py-20 flex flex-col items-center">
            <div className="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center text-primary mb-4">
              <Luggage size={32} />
            </div>
            <h3 className="text-xl font-bold text-black dark:text-white mb-1">No Packing Items</h3>
            <p className="text-gray-500 text-xs max-w-xs">
              Click 'Regenerate Suggestions' or 'Add Custom Item' to start packing!
            </p>
          </div>
        ) : (
          categories.map(category => {
            const catItems = items.filter(i => i.category === category);
            if (catItems.length === 0) return null;

            return (
              <section key={category} className="space-y-3">
                <h3 className="text-xs font-bold text-gray-400 uppercase tracking-widest ml-1">
                  {category}
                </h3>

                <div className="space-y-2">
                  {catItems.map(item => (
                    <div
                      key={item.id}
                      onClick={() => handleToggle(item.id)}
                      className={`card p-4 flex justify-between items-center cursor-pointer transition-all ${
                        item.isPacked
                          ? 'bg-gray-50/50 dark:bg-surface-variant/20 border-transparent opacity-60'
                          : 'hover:border-primary/20 hover:shadow-md'
                      }`}
                    >
                      <div className="flex items-center gap-3">
                        <div
                          className={`w-6 h-6 rounded-lg border-2 flex items-center justify-center transition-all ${
                            item.isPacked
                              ? 'bg-primary border-primary text-white'
                              : 'border-gray-300 dark:border-surface-variant'
                          }`}
                        >
                          {item.isPacked && <Check size={14} strokeWidth={3} />}
                        </div>

                        <div>
                          <h4
                            className={`font-bold text-sm text-black dark:text-white ${
                              item.isPacked ? 'line-through text-gray-400' : ''
                            }`}
                          >
                            {item.name}
                          </h4>
                          {item.quantity && (
                            <span className="text-[11px] font-semibold text-primary block mt-0.5">
                              Qty: {item.quantity}
                            </span>
                          )}
                        </div>
                      </div>

                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDelete(item.id);
                        }}
                        className="p-2 text-gray-300 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 rounded-xl transition-colors"
                        title="Delete Item"
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>
                  ))}
                </div>
              </section>
            );
          })
        )}
      </div>

      {/* Add Custom Item Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-6 bg-black/50 backdrop-blur-sm">
          <div className="bg-white dark:bg-surface-dark w-full max-w-md rounded-[32px] p-8 shadow-2xl relative">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-black text-black dark:text-white">Add Custom Item</h2>
              <button
                onClick={() => setShowAddModal(false)}
                className="p-2 text-gray-400 hover:text-black dark:hover:text-white rounded-full"
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleAddCustom} className="space-y-5">
              <div>
                <label className="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2 ml-1">
                  Item Name
                </label>
                <input
                  type="text"
                  value={customName}
                  onChange={(e) => setCustomName(e.target.value)}
                  placeholder="e.g. Camera Charger, Swim Goggles"
                  className="input-field"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2 ml-1">
                  Quantity
                </label>
                <input
                  type="text"
                  value={customQuantity}
                  onChange={(e) => setCustomQuantity(e.target.value)}
                  placeholder="e.g. 1, 2 pairs, 1 kit"
                  className="input-field"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2 ml-1">
                  Category
                </label>
                <select
                  value={customCategory}
                  onChange={(e) => setCustomCategory(e.target.value)}
                  className="input-field bg-white dark:bg-surface-dark"
                >
                  {categories.map(cat => (
                    <option key={cat} value={cat}>{cat}</option>
                  ))}
                </select>
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="flex-1 px-6 py-3.5 rounded-2xl font-bold text-gray-500 hover:bg-gray-100 dark:hover:bg-surface-variant transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 btn-primary py-3.5 shadow-lg shadow-primary/20"
                >
                  Add Item
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default PackingChecklistScreen;
