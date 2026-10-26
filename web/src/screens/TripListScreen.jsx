import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Trash2, ArrowLeft, Plus } from 'lucide-react';
import TripService from '../data/services/TripService';
import { getEffectiveStatus } from '../utils/tripGenerator';

const TripListScreen = () => {
  const navigate = useNavigate();
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [tripToDelete, setTripToDelete] = useState(null);

  const fetchTrips = async () => {
    setLoading(true);
    const result = await TripService.getTrips();
    if (result.isSuccess) {
      setTrips(result.trips);
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchTrips();
    const unsubscribe = TripService.subscribeToTrips((updatedTrips) => {
      setTrips(updatedTrips);
    });
    return () => unsubscribe();
  }, []);

  const handleDelete = async () => {
    if (!tripToDelete) return;
    const result = await TripService.deleteTrip(tripToDelete.id);
    if (result.isSuccess) {
      setTrips(trips.filter(t => t.id !== tripToDelete.id));
      setTripToDelete(null);
    }
  };

  const activeTrips = trips.filter(t => getEffectiveStatus(t) === 'Active');
  const upcomingTrips = trips.filter(t => getEffectiveStatus(t) === 'Upcoming');
  const pastTrips = trips.filter(t => getEffectiveStatus(t) === 'Completed');

  const renderTripCard = (trip) => {
    const status = getEffectiveStatus(trip);
    const badgeStyle = status === 'Completed'
      ? 'bg-secondary/10 text-secondary'
      : status === 'Active'
      ? 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400'
      : 'bg-primary/10 text-primary';

    return (
      <div
        key={trip.id}
        className="card group cursor-pointer active:scale-[0.99] transition-transform overflow-hidden"
        onClick={() => navigate(`/trip-details/${trip.id}`)}
      >
        <div className="p-5 flex justify-between items-center">
          <div className="flex-1">
            <h3 className="text-xl font-bold text-black dark:text-white mb-1">{trip.name}</h3>
            <p className="text-sm text-gray-500 mb-2">{trip.dates}</p>
            <div className="flex items-center gap-2 text-xs font-bold text-primary">
              <span>{trip.members} Members</span>
              {trip.total_budget > 0 && (
                <span className="text-gray-400">&bull; {trip.currency === 'INR' ? '₹' : '$'}{trip.total_budget.toLocaleString()}</span>
              )}
            </div>
          </div>

          <div className="flex items-center gap-3">
            <span className={`px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider ${badgeStyle}`}>
              {status}
            </span>

            <button
              onClick={(e) => {
                e.stopPropagation();
                setTripToDelete(trip);
              }}
              className="p-2 text-gray-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 rounded-xl transition-colors"
            >
              <Trash2 size={20} />
            </button>
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="min-h-screen bg-white dark:bg-background-dark pb-24">
      {/* Header */}
      <div className="px-6 py-8 flex items-center justify-between">
        <div className="flex items-center gap-4">
          <button onClick={() => navigate('/dashboard')} className="p-2 hover:bg-gray-100 dark:hover:bg-surface-variant rounded-full transition-colors">
            <ArrowLeft size={24} />
          </button>
          <h1 className="text-3xl font-black text-black dark:text-white">My Trips</h1>
        </div>
        <button
          onClick={() => navigate('/create-trip')}
          className="w-10 h-10 rounded-full bg-primary flex items-center justify-center text-white shadow-lg shadow-primary/20"
        >
          <Plus size={24} />
        </button>
      </div>

      <div className="px-6 space-y-8">
        {loading ? (
          <div className="flex justify-center py-12">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          </div>
        ) : trips.length === 0 ? (
          <div className="text-center py-20">
            <p className="text-gray-500 mb-6">No trips added yet</p>
            <button onClick={() => navigate('/create-trip')} className="btn-secondary">
              Plan Your First Trip
            </button>
          </div>
        ) : (
          <>
            {/* Ongoing Trips Section */}
            {activeTrips.length > 0 && (
              <section className="space-y-3">
                <h2 className="text-xs font-bold text-gray-400 uppercase tracking-widest">Ongoing Trips</h2>
                {activeTrips.map(renderTripCard)}
              </section>
            )}

            {/* Upcoming Trips Section */}
            <section className="space-y-3">
              <h2 className="text-xs font-bold text-gray-400 uppercase tracking-widest">Upcoming Trips</h2>
              {upcomingTrips.length === 0 ? (
                <div className="p-4 bg-gray-50 dark:bg-surface-variant/30 rounded-2xl text-xs text-gray-500">
                  No upcoming trips at the moment.
                </div>
              ) : (
                upcomingTrips.map(renderTripCard)
              )}
            </section>

            {/* Past Trips Section */}
            <section className="space-y-3">
              <h2 className="text-xs font-bold text-gray-400 uppercase tracking-widest">Past Trips</h2>
              {pastTrips.length === 0 ? (
                <div className="p-4 bg-gray-50 dark:bg-surface-variant/30 rounded-2xl text-xs text-gray-500">
                  No past trips yet. Your completed adventures will appear here!
                </div>
              ) : (
                pastTrips.map(renderTripCard)
              )}
            </section>
          </>
        )}
      </div>

      {/* Delete Confirmation Modal */}
      {tripToDelete && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-6 bg-black/50 backdrop-blur-sm">
          <div className="bg-white dark:bg-surface-dark w-full max-w-sm rounded-[32px] p-8 shadow-2xl">
            <h2 className="text-2xl font-black text-black dark:text-white mb-4">Delete Trip?</h2>
            <p className="text-gray-500 dark:text-gray-400 mb-8">
              Are you sure you want to delete your trip to <span className="font-bold text-black dark:text-white">"{tripToDelete.name}"</span>?
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => setTripToDelete(null)}
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
    </div>
  );
};

export default TripListScreen;
