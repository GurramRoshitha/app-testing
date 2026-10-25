import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Plane, Sparkles, X, CheckCircle, MapPin } from 'lucide-react';

const TripReminderPopup = ({ popup, onDismiss }) => {
  const navigate = useNavigate();

  if (!popup) return null;

  const isCompleted = popup.type === 'TRIP_COMPLETED';

  const handleViewTrip = () => {
    onDismiss(popup.popupId);
    if (popup.tripId) {
      navigate(`/trip-details/${popup.tripId}`);
    }
  };

  return (
    <div className="fixed top-6 left-1/2 -translate-x-1/2 z-[200] w-[92%] max-w-md animate-bounce-in">
      <div className="bg-white dark:bg-surface-dark border-2 border-primary/20 dark:border-surface-variant rounded-[28px] p-5 shadow-2xl relative overflow-hidden backdrop-blur-xl">
        {/* Top Accent Line */}
        <div className={`absolute top-0 left-0 right-0 h-1.5 ${isCompleted ? 'bg-secondary' : 'bg-primary'}`}></div>

        <button
          onClick={() => onDismiss(popup.popupId)}
          className="absolute top-4 right-4 p-1.5 text-gray-400 hover:text-black dark:hover:text-white hover:bg-gray-100 dark:hover:bg-surface-variant rounded-full transition-colors"
          title="Dismiss"
        >
          <X size={18} />
        </button>

        <div className="flex gap-4 items-start pr-6">
          <div className={`w-12 h-12 rounded-2xl flex items-center justify-center shrink-0 ${
            isCompleted ? 'bg-secondary/10 text-secondary' : 'bg-primary/10 text-primary'
          }`}>
            {isCompleted ? <CheckCircle size={26} /> : <Plane size={26} />}
          </div>

          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-2 mb-1">
              <span className="text-[10px] font-black uppercase tracking-wider px-2.5 py-0.5 rounded-full bg-primary/10 text-primary">
                {isCompleted ? 'Completed' : 'Trip Reminder'}
              </span>
            </div>

            <h4 className="text-lg font-black text-black dark:text-white leading-tight mb-1">
              {popup.title}
            </h4>

            <p className="text-xs text-gray-600 dark:text-gray-300 leading-relaxed mb-4">
              {popup.message}
            </p>

            <div className="flex gap-2">
              <button
                onClick={handleViewTrip}
                className="btn-primary text-xs px-4 py-2 flex items-center gap-1.5 shadow-md shadow-primary/20"
              >
                <MapPin size={14} />
                <span>View Details</span>
              </button>
              <button
                onClick={() => onDismiss(popup.popupId)}
                className="px-4 py-2 rounded-2xl text-xs font-bold text-gray-500 hover:bg-gray-100 dark:hover:bg-surface-variant transition-colors"
              >
                Dismiss
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TripReminderPopup;
