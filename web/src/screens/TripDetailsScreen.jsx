import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Trash2,
  Calendar,
  Hotel,
  Utensils,
  Clock,
  Sparkles,
  CloudSun,
  MapPin,
  Users,
  Wallet,
  Compass,
  Printer,
  FileText,
  Luggage,
  X,
  Star,
  Edit3
} from 'lucide-react';
import TripService from '../data/services/TripService';
import { exportTripToPdf, getEffectiveStatus } from '../utils/tripGenerator';

const TripDetailsScreen = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [trip, setTrip] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedDay, setSelectedDay] = useState("1");
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showCompleteModal, setShowCompleteModal] = useState(false);

  // Review & Rating State
  const [review, setReview] = useState(null);
  const [showRatingModal, setShowRatingModal] = useState(false);
  const [rating, setRating] = useState(5);
  const [reviewText, setReviewText] = useState('');
  const [ratingError, setRatingError] = useState('');

  const reviewStorageKey = `voyage_review_${id}`;

  const handleUpdateStatus = async (newStatus) => {
    if (!trip) return;
    const result = await TripService.updateTripStatus(trip.id, newStatus);
    if (result.isSuccess) {
      setTrip({ ...trip, status: newStatus });
      setShowCompleteModal(false);
    }
  };

  useEffect(() => {
    const fetchTrip = async () => {
      setLoading(true);
      const result = await TripService.getTripById(id);
      if (result.isSuccess) {
        setTrip(result.trip);
        const days = Object.keys(result.trip.itinerary || {}).sort((a, b) => parseInt(a) - parseInt(b));
        if (days.length > 0) setSelectedDay(days[0]);

        // Load saved review
        const savedReview = JSON.parse(localStorage.getItem(reviewStorageKey) || 'null');
        if (savedReview) {
          setReview(savedReview);
          setRating(savedReview.rating || 5);
          setReviewText(savedReview.reviewText || '');
        }
      } else {
        navigate('/dashboard');
      }
      setLoading(false);
    };
    fetchTrip();
  }, [id, navigate, reviewStorageKey]);

  const handleDelete = async () => {
    const result = await TripService.deleteTrip(id);
    if (result.isSuccess) {
      navigate('/trip-list');
    }
  };

  const handleSaveReview = (e) => {
    e.preventDefault();
    setRatingError('');

    if (rating < 1) {
      setRatingError('Please select at least 1 star');
      return;
    }

    const reviewObj = {
      id: review?.id || `rev_${Date.now()}`,
      tripId: id,
      destinationName: trip?.name || '',
      rating: rating,
      reviewText: reviewText.trim(),
      timestamp: Date.now()
    };

    setReview(reviewObj);
    localStorage.setItem(reviewStorageKey, JSON.stringify(reviewObj));
    setShowRatingModal(false);
  };

  const getDayActivities = () => {
    if (!trip || !trip.itinerary) return [];
    return trip.itinerary[selectedDay] || trip.itinerary[parseInt(selectedDay)] || [];
  };

  const getActivityIcon = (type) => {
    const t = type?.toLowerCase() || '';
    if (t.includes('hotel') || t.includes('stay') || t.includes('accommodation')) return <Hotel size={18} />;
    if (t.includes('restaurant') || t.includes('lunch') || t.includes('dinner') || t.includes('food')) return <Utensils size={18} />;
    return <Clock size={18} />;
  };

  const getAccentColor = (type) => {
    const t = type?.toLowerCase() || '';
    if (t.includes('hotel') || t.includes('stay') || t.includes('accommodation')) return 'text-primary bg-primary/10 border-primary/20';
    if (t.includes('restaurant') || t.includes('lunch') || t.includes('dinner') || t.includes('food')) return 'text-secondary bg-secondary/10 border-secondary/20';
    return 'text-gray-500 bg-gray-100 border-gray-200';
  };

  const getDotColor = (type) => {
    const t = type?.toLowerCase() || '';
    if (t.includes('hotel') || t.includes('stay') || t.includes('accommodation')) return 'bg-primary';
    if (t.includes('restaurant') || t.includes('lunch') || t.includes('dinner') || t.includes('food')) return 'bg-secondary';
    return 'bg-gray-400';
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-white dark:bg-background-dark">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
    );
  }

  if (!trip) return null;

  const days = Object.keys(trip.itinerary || {}).sort((a, b) => parseInt(a) - parseInt(b));
  const isCompleted = getEffectiveStatus(trip) === 'Completed';

  return (
    <div className="min-h-screen bg-white dark:bg-background-dark pb-24 relative">
      {/* Top Bar */}
      <div className="px-6 py-4 flex items-center justify-between sticky top-0 bg-white/80 dark:bg-background-dark/80 backdrop-blur-md z-40">
        <button onClick={() => navigate(-1)} className="p-2 hover:bg-gray-100 dark:hover:bg-surface-variant rounded-full transition-colors">
          <ArrowLeft size={24} />
        </button>
        <div className="flex gap-2">
          <button
            onClick={() => exportTripToPdf(trip)}
            title="Export Itinerary as PDF"
            className="p-2 text-primary hover:bg-primary/5 rounded-full transition-colors flex items-center gap-1.5 font-bold text-xs"
          >
            <FileText size={22} />
          </button>
          <button onClick={() => navigate(`/weather/${trip.name}`)} className="p-2 text-primary hover:bg-primary/5 rounded-full transition-colors">
            <CloudSun size={24} />
          </button>
          <button onClick={() => setShowDeleteModal(true)} className="p-2 text-red-500 hover:bg-red-50 rounded-full transition-colors">
            <Trash2 size={24} />
          </button>
        </div>
      </div>

      {/* Header */}
      <div className="px-6 py-6 mb-4 flex justify-between items-end">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <Sparkles className="text-secondary" size={24} />
            <h1 className="text-3xl font-black text-black dark:text-white font-serif">AI Smart Planner</h1>
          </div>
          <div className="flex items-center gap-3">
            <p className="text-gray-500 dark:text-gray-400 text-lg">
              Itinerary for {trip.name}
            </p>
            <span className={`px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider ${
              isCompleted
              ? 'bg-secondary/10 text-secondary'
              : getEffectiveStatus(trip) === 'Active'
              ? 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400'
              : 'bg-primary/10 text-primary'
            }`}>
              {getEffectiveStatus(trip)}
            </span>
          </div>
        </div>

        <div className="flex gap-2">
          {isCompleted && (
            <button
              onClick={() => {
                setRatingError('');
                setShowRatingModal(true);
              }}
              className="btn-primary text-xs px-4 py-2 flex items-center gap-1.5 shadow-md"
            >
              <Star size={16} className="fill-white" />
              <span>{review ? "Edit Review" : "Rate Your Trip"}</span>
            </button>
          )}

          {!isCompleted ? (
            <button
              onClick={() => setShowCompleteModal(true)}
              className="px-4 py-2 bg-secondary/10 text-secondary hover:bg-secondary/20 rounded-2xl text-xs font-bold transition-all shadow-sm flex items-center gap-1.5"
            >
              Mark as Completed
            </button>
          ) : (
            <button
              onClick={() => handleUpdateStatus("Upcoming")}
              className="px-4 py-2 bg-gray-100 dark:bg-surface-variant text-gray-600 dark:text-gray-300 rounded-2xl text-xs font-bold transition-all shadow-sm flex items-center gap-1.5"
            >
              Move to Upcoming
            </button>
          )}

          <button
            onClick={() => navigate(`/budget/${trip.id}`)}
            className="btn-secondary text-xs px-4 py-2 flex items-center gap-2 shadow-md"
            title="Group Cost Split"
          >
            <Wallet size={16} />
            <span>Budget & Split</span>
          </button>
          <button
            onClick={() => navigate(`/packing-checklist/${trip.id}`)}
            className="btn-secondary text-xs px-4 py-2 flex items-center gap-2 shadow-md"
            title="Packing Checklist"
          >
            <Luggage size={16} />
            <span>Packing Checklist</span>
          </button>
          <button
            onClick={() => exportTripToPdf(trip)}
            className="btn-primary text-xs px-4 py-2 flex items-center gap-2 shadow-md"
          >
            <Printer size={16} />
            <span>Export PDF</span>
          </button>
        </div>
      </div>

      {/* --- COMPLETED TRIP RATING & REVIEW CARD --- */}
      {isCompleted && (
        <div className="px-6 mb-8">
          <div className="card p-6 border border-amber-200 dark:border-amber-900/40 bg-amber-50/40 dark:bg-navy-card shadow-md">
            <div className="flex justify-between items-center mb-4">
              <div className="flex items-center gap-2">
                <Star className="text-amber-500 fill-amber-500" size={22} />
                <h3 className="text-xl font-extrabold text-slate-900 dark:text-white">
                  Trip Rating & Review
                </h3>
              </div>

              <button
                onClick={() => {
                  setRatingError('');
                  setShowRatingModal(true);
                }}
                className="text-xs font-bold text-primary hover:underline flex items-center gap-1"
              >
                <Edit3 size={14} />
                <span>{review ? "Edit Review" : "Write Review"}</span>
              </button>
            </div>

            {review ? (
              <div className="space-y-3">
                <div className="flex items-center gap-1">
                  {[1, 2, 3, 4, 5].map((star) => (
                    <Star
                      key={star}
                      size={20}
                      className={star <= review.rating ? "text-amber-500 fill-amber-500" : "text-slate-300 dark:text-slate-700"}
                    />
                  ))}
                  <span className="text-sm font-black text-slate-900 dark:text-white ml-2">
                    {review.rating}.0 / 5.0
                  </span>
                </div>

                {review.reviewText ? (
                  <p className="text-sm text-slate-700 dark:text-slate-300 italic bg-white dark:bg-slate-900/50 p-4 rounded-2xl border border-amber-100 dark:border-slate-800">
                    "{review.reviewText}"
                  </p>
                ) : (
                  <p className="text-xs text-slate-400 italic">No written review provided.</p>
                )}

                <span className="text-[10px] text-slate-400 block font-medium">
                  Reviewed on {new Date(review.timestamp).toLocaleDateString()}
                </span>
              </div>
            ) : (
              <div className="text-center py-4">
                <p className="text-sm text-slate-600 dark:text-slate-400 font-medium mb-3">
                  How was your journey to <span className="font-bold text-slate-900 dark:text-white">{trip.name}</span>? Share your experience!
                </p>
                <button
                  onClick={() => setShowRatingModal(true)}
                  className="btn-primary text-xs px-6 py-2.5 mx-auto"
                >
                  Rate Your Trip
                </button>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Day Tabs */}
      <div className="flex gap-4 overflow-x-auto px-6 pb-2 no-scrollbar border-b border-gray-100 dark:border-surface-variant mb-8">
        {days.map((day) => (
          <button
            key={day}
            onClick={() => setSelectedDay(day)}
            className={`px-6 py-3 font-bold text-sm transition-all whitespace-nowrap relative ${
              selectedDay === day
              ? 'text-primary'
              : 'text-gray-400 hover:text-gray-600'
            }`}
          >
            Day {day}
            {selectedDay === day && (
              <div className="absolute bottom-0 left-0 right-0 h-1 bg-primary rounded-t-full"></div>
            )}
          </button>
        ))}
      </div>

      {/* Itinerary List */}
      <div className="px-6 space-y-8 relative">
        <div className="absolute left-[39px] top-6 bottom-6 w-0.5 bg-gray-100 dark:bg-surface-variant"></div>

        {getDayActivities().length > 0 ? (
          getDayActivities().map((activity, index) => (
            <div key={index} className="flex gap-6 relative">
              <div className="flex flex-col items-center mt-6 z-10">
                <div className={`w-3.5 h-3.5 rounded-full border-[3px] border-white dark:border-background-dark ring-1 ring-gray-100 dark:ring-surface-variant ${getDotColor(activity.type)}`}></div>
              </div>

              <div className="flex-1 bg-gray-50 dark:bg-surface-dark rounded-[24px] p-5 shadow-sm border border-gray-100 dark:border-surface-variant">
                <div className="flex justify-between items-start mb-4">
                  <div className="flex items-center gap-3">
                    <div className={`p-2 rounded-xl border ${getAccentColor(activity.type)}`}>
                      {getActivityIcon(activity.type)}
                    </div>
                    <span className="text-lg font-black text-black dark:text-white">{activity.time}</span>
                  </div>
                  {activity.cost && (
                    <div className="px-3 py-1 bg-primary/5 text-primary rounded-full text-[10px] font-black tracking-widest">
                      {activity.cost}
                    </div>
                  )}
                </div>

                <h4 className="text-2xl font-black text-black dark:text-white mb-1 leading-tight">{activity.title}</h4>
                <div className="flex items-center gap-2 mb-4 text-[10px] font-black uppercase tracking-wider text-primary opacity-80">
                  <span>{activity.type}</span>
                  <span className="text-gray-300">•</span>
                  <span>{activity.category}</span>
                </div>

                <p className="text-gray-500 dark:text-gray-400 text-sm leading-relaxed">
                  {activity.description}
                </p>
              </div>
            </div>
          ))
        ) : (
          <div className="text-center py-20">
            <Compass size={48} className="mx-auto text-gray-200 mb-4" />
            <p className="text-gray-400">No activities scheduled for this day.</p>
          </div>
        )}
      </div>

      {/* Rate & Review Modal */}
      {showRatingModal && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-6 bg-black/50 backdrop-blur-sm">
          <div className="bg-white dark:bg-navy-card w-full max-w-md rounded-[32px] p-8 shadow-2xl relative border border-slate-200 dark:border-slate-800">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-black text-slate-900 dark:text-white">Rate Your Trip</h2>
              <button
                onClick={() => setShowRatingModal(false)}
                className="p-2 text-slate-400 hover:text-slate-900 dark:hover:text-white rounded-full"
              >
                <X size={20} />
              </button>
            </div>

            {ratingError && (
              <div className="mb-4 p-3 bg-red-50 text-red-600 rounded-xl text-xs font-bold">
                {ratingError}
              </div>
            )}

            <form onSubmit={handleSaveReview} className="space-y-6">
              {/* Star Rating Picker */}
              <div className="flex flex-col items-center gap-2">
                <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">
                  Select Rating
                </span>
                <div className="flex gap-2">
                  {[1, 2, 3, 4, 5].map((star) => (
                    <button
                      key={star}
                      type="button"
                      onClick={() => setRating(star)}
                      className="p-1 transition-transform active:scale-110"
                    >
                      <Star
                        size={36}
                        className={star <= rating ? "text-amber-500 fill-amber-500" : "text-slate-200 dark:text-slate-700"}
                      />
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2 ml-1">
                  Write Your Review (Optional)
                </label>
                <textarea
                  rows="4"
                  value={reviewText}
                  onChange={(e) => setReviewText(e.target.value)}
                  placeholder="Share your personal experience, favorite spots, or tips for other travelers..."
                  className="input-field resize-none"
                ></textarea>
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowRatingModal(false)}
                  className="flex-1 px-6 py-3.5 rounded-2xl font-bold text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 btn-primary py-3.5 shadow-lg shadow-primary/20"
                >
                  Submit Review
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Trip Confirmation Modal */}
      {showDeleteModal && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-6 bg-black/50 backdrop-blur-sm">
          <div className="bg-white dark:bg-surface-dark w-full max-w-sm rounded-[32px] p-8 shadow-2xl">
            <h2 className="text-2xl font-black text-black dark:text-white mb-4">Delete Trip?</h2>
            <p className="text-gray-500 dark:text-gray-400 mb-8 leading-relaxed">
              This will permanently remove your <span className="font-bold text-black dark:text-white">"{trip.name}"</span> journey. This action cannot be undone.
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => setShowDeleteModal(false)}
                className="flex-1 px-6 py-3 rounded-2xl font-bold text-gray-500 hover:bg-gray-100 dark:hover:bg-surface-variant transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleDelete}
                className="flex-1 px-6 py-3 rounded-2xl font-bold bg-red-500 text-white shadow-lg shadow-red-500/20 active:scale-95 transition-all"
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Complete Trip Confirmation Modal */}
      {showCompleteModal && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-6 bg-black/50 backdrop-blur-sm">
          <div className="bg-white dark:bg-surface-dark w-full max-w-sm rounded-[32px] p-8 shadow-2xl">
            <h2 className="text-2xl font-black text-black dark:text-white mb-4">Complete This Trip?</h2>
            <p className="text-gray-500 dark:text-gray-400 mb-8 leading-relaxed">
              Are you sure you want to move <span className="font-bold text-black dark:text-white">"{trip.name}"</span> to Past Trips?
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => setShowCompleteModal(false)}
                className="flex-1 px-6 py-3 rounded-2xl font-bold text-gray-500 hover:bg-gray-100 dark:hover:bg-surface-variant transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={() => handleUpdateStatus("Completed")}
                className="flex-1 px-6 py-3 rounded-2xl font-bold bg-primary text-white shadow-lg shadow-primary/20 active:scale-95 transition-all"
              >
                Mark Completed
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TripDetailsScreen;
