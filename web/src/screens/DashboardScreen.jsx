import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Home,
  Map as MapIcon,
  Plus,
  Luggage,
  Wallet,
  Sun,
  Bell,
  Search,
  SlidersHorizontal,
  ArrowUpRight,
  User,
  Clock,
  CheckCircle2
} from 'lucide-react';
import AuthService from '../data/services/AuthService';
import TripService from '../data/services/TripService';
import { getEffectiveStatus } from '../utils/tripGenerator';
import { syncNotificationsFromTrips, getUnreadCount, checkAndGetActivePopups, markPopupAsDismissed } from '../utils/notificationService';
import TripReminderPopup from '../components/TripReminderPopup';

const getDestinationImage = (destinationName) => {
  const name = (destinationName || '').toLowerCase();
  if (name.includes('goa')) return "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=600&q=80";
  if (name.includes('munnar') || name.includes('kerala')) return "https://images.unsplash.com/photo-1593693397690-362cb9666fc2?auto=format&fit=crop&w=600&q=80";
  if (name.includes('ooty')) return "https://images.unsplash.com/photo-1544735716-392fe2489ffa?auto=format&fit=crop&w=600&q=80";
  if (name.includes('jaipur')) return "https://images.unsplash.com/photo-1599661046289-e31897846e41?auto=format&fit=crop&w=600&q=80";
  if (name.includes('telangana') || name.includes('hyderabad')) return "https://images.unsplash.com/photo-1605367031760-80236055d7f1?auto=format&fit=crop&w=600&q=80";
  if (name.includes('shimla') || name.includes('manali') || name.includes('kashmir') || name.includes('ladakh') || name.includes('mountain')) return "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=600&q=80";
  if (name.includes('beach') || name.includes('andaman') || name.includes('gokarna')) return "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=600&q=80";
  if (name.includes('varanasi') || name.includes('puri') || name.includes('temple')) return "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?auto=format&fit=crop&w=600&q=80";
  return "https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=600&q=80";
};

const DashboardScreen = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [allTrips, setAllTrips] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [activePopups, setActivePopups] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let unsubscribe = () => {};

    const loadData = async () => {
      const currentUser = AuthService.getCurrentUser();
      if (!currentUser) {
        navigate('/login');
        return;
      }

      const savedProfile = JSON.parse(localStorage.getItem(`voyage_profile_${currentUser.uid}`) || '{}');
      const displayName = savedProfile.fullName || currentUser.displayName || 'Aarav';
      const photoUri = savedProfile.photoUri || currentUser.photoURL || '';

      setUser({ ...currentUser, displayName, photoUri });

      // Initial load from local/getTrips
      const tripsResult = await TripService.getTrips();
      if (tripsResult.isSuccess && tripsResult.trips.length > 0) {
        setAllTrips(tripsResult.trips);
        syncNotificationsFromTrips(tripsResult.trips);

        const popups = checkAndGetActivePopups(tripsResult.trips);
        setActivePopups(popups);
      }

      // Real-Time Firestore Subscription
      unsubscribe = TripService.subscribeToTrips((updatedTrips) => {
        setAllTrips(updatedTrips);
        syncNotificationsFromTrips(updatedTrips);
        const popups = checkAndGetActivePopups(updatedTrips);
        setActivePopups(popups);
      });

      setUnreadCount(getUnreadCount());
      setLoading(false);
    };

    loadData();

    return () => unsubscribe();
  }, [navigate]);

  const handleDismissPopup = (popupId) => {
    markPopupAsDismissed(popupId);
    setActivePopups(prev => prev.filter(p => p.popupId !== popupId));
  };

  // Group trips accurately by date-calculated status
  const activeTrips = allTrips.filter(t => getEffectiveStatus(t) === 'Active');
  const upcomingTrips = allTrips.filter(t => getEffectiveStatus(t) === 'Upcoming');
  const completedTrips = allTrips.filter(t => getEffectiveStatus(t) === 'Completed');

  // Fallback if no completed trips yet
  const recentTrips = completedTrips.length > 0
    ? completedTrips
    : allTrips.filter(t => t.id !== activeTrips[0]?.id && t.id !== upcomingTrips[0]?.id);

  const getInitials = (name) => {
    if (!name) return 'AS';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50 dark:bg-navy">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-navy text-slate-800 dark:text-slate-100 pb-28">
      {/* Pop-up Reminder Banner */}
      {activePopups.length > 0 && (
        <TripReminderPopup popup={activePopups[0]} onDismiss={handleDismissPopup} />
      )}

      {/* Top Header Row */}
      <div className="px-6 pt-10 pb-4 flex justify-between items-center max-w-xl mx-auto">
        <div className="flex items-center gap-3">
          <div
            onClick={() => navigate('/profile')}
            className="w-12 h-12 rounded-full bg-mint text-primary dark:bg-mint-dark dark:text-teal-300 font-bold flex items-center justify-center cursor-pointer overflow-hidden border border-teal-100 dark:border-teal-900"
          >
            {user?.photoUri ? (
              <img src={user.photoUri} alt="Profile" className="w-full h-full object-cover" />
            ) : (
              <span className="text-base font-extrabold">{getInitials(user?.displayName)}</span>
            )}
          </div>
          <div>
            <span className="text-xs font-medium text-slate-400 dark:text-slate-400 block">Good morning,</span>
            <h1 className="text-2xl font-black text-slate-900 dark:text-white flex items-center gap-1.5">
              {user?.displayName?.split(' ')[0] || 'Aarav'}
              <span className="text-xl">👋</span>
            </h1>
          </div>
        </div>

        <button
          onClick={() => navigate('/notifications')}
          className="relative p-3 rounded-full bg-white dark:bg-navy-card shadow-sm border border-slate-100 dark:border-slate-800 text-slate-700 dark:text-slate-200"
          title="Notifications"
        >
          <Bell size={20} />
          {unreadCount > 0 && (
            <span className="absolute top-2 right-2 w-2.5 h-2.5 bg-primary rounded-full ring-2 ring-white dark:ring-navy"></span>
          )}
        </button>
      </div>

      <div className="px-6 space-y-6 mt-2 max-w-xl mx-auto">
        {/* Plan a New Trip Card Banner */}
        <div
          onClick={() => navigate('/create-trip')}
          className="rounded-[28px] bg-[#0E2A3A] dark:bg-navy-card p-6 text-white shadow-xl cursor-pointer active:scale-[0.99] transition-transform relative overflow-hidden flex justify-between items-center"
        >
          <div className="relative z-10 pr-4">
            <h3 className="text-2xl font-black mb-1">Plan a New Trip</h3>
            <p className="text-xs text-slate-300 font-medium">
              Your ideas. A little AI. Endless possibility.
            </p>
          </div>
          <button className="w-12 h-12 rounded-full bg-primary text-white flex items-center justify-center shrink-0 shadow-lg shadow-primary/30">
            <Plus size={24} />
          </button>
        </div>

        {/* 4 Quick Action Icon Pills */}
        <div className="grid grid-cols-4 gap-3">
          {[
            { label: "My Trips", icon: MapIcon, route: "/trip-list" },
            { label: "Packing", icon: Luggage, route: allTrips[0] ? `/packing-checklist/${allTrips[0].id}` : "/trip-list" },
            { label: "Budget", icon: Wallet, route: allTrips[0] ? `/budget/${allTrips[0].id}` : "/budget" },
            { label: "Weather", icon: Sun, route: allTrips[0] ? `/weather/${allTrips[0].name}` : "/weather/Munnar" }
          ].map((item) => (
            <button
              key={item.label}
              onClick={() => navigate(item.route)}
              className="flex flex-col items-center gap-2 group"
            >
              <div className="w-16 h-16 rounded-[22px] bg-mint dark:bg-mint-dark/50 text-primary dark:text-teal-300 flex items-center justify-center transition-transform group-active:scale-95 shadow-sm">
                <item.icon size={24} />
              </div>
              <span className="text-xs font-semibold text-slate-600 dark:text-slate-300">{item.label}</span>
            </button>
          ))}
        </div>

        {/* --- SECTION 1: CURRENT / ACTIVE TRIPS --- */}
        {activeTrips.length > 0 && (
          <div>
            <div className="flex justify-between items-center mb-3">
              <div className="flex items-center gap-2">
                <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse"></span>
                <h2 className="text-xl font-extrabold text-slate-900 dark:text-white">Current Trip</h2>
              </div>
              <button onClick={() => navigate('/trip-list')} className="text-xs font-bold text-primary hover:underline">See all</button>
            </div>

            {activeTrips.map(trip => (
              <div
                key={trip.id || trip.name}
                onClick={() => navigate(`/trip-details/${trip.id}`)}
                className="relative rounded-[28px] overflow-hidden shadow-lg group cursor-pointer h-52 bg-slate-800 mb-3"
              >
                <img
                  src={getDestinationImage(trip.name)}
                  alt={trip.name}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-slate-950/30 to-transparent p-6 flex flex-col justify-between">
                  <div className="flex justify-end">
                    <span className="bg-emerald-500/30 backdrop-blur-md text-emerald-300 text-[10px] font-black uppercase px-3 py-1 rounded-full border border-emerald-500/40">
                      ACTIVE
                    </span>
                  </div>
                  <div className="flex justify-between items-end">
                    <div>
                      <h3 className="text-2xl font-black text-white mb-1 capitalize">{trip.name}</h3>
                      <p className="text-xs text-slate-200 font-medium">
                        {trip.dates} &bull; {trip.members || 1} travellers
                      </p>
                    </div>
                    <div className="w-10 h-10 rounded-full bg-white/20 backdrop-blur-md flex items-center justify-center text-white">
                      <ArrowUpRight size={20} />
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* --- SECTION 2: UPCOMING TRIPS --- */}
        <div>
          <div className="flex justify-between items-center mb-3">
            <h2 className="text-xl font-extrabold text-slate-900 dark:text-white">Upcoming Trips</h2>
            <button onClick={() => navigate('/trip-list')} className="text-xs font-bold text-primary hover:underline">See all</button>
          </div>

          {upcomingTrips.length > 0 ? (
            upcomingTrips.map(trip => (
              <div
                key={trip.id || trip.name}
                onClick={() => navigate(`/trip-details/${trip.id}`)}
                className="relative rounded-[28px] overflow-hidden shadow-lg group cursor-pointer h-52 bg-slate-800 mb-3"
              >
                <img
                  src={getDestinationImage(trip.name)}
                  alt={trip.name}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-slate-950/30 to-transparent p-6 flex flex-col justify-between">
                  <div className="flex justify-end">
                    <span className="bg-teal-500/20 backdrop-blur-md text-teal-200 text-[10px] font-black uppercase px-3 py-1 rounded-full border border-teal-500/30">
                      UPCOMING
                    </span>
                  </div>
                  <div className="flex justify-between items-end">
                    <div>
                      <h3 className="text-2xl font-black text-white mb-1 capitalize">{trip.name}</h3>
                      <p className="text-xs text-slate-200 font-medium">
                        {trip.dates} &bull; {trip.members || 1} travellers
                      </p>
                    </div>
                    <div className="w-10 h-10 rounded-full bg-white/20 backdrop-blur-md flex items-center justify-center text-white">
                      <ArrowUpRight size={20} />
                    </div>
                  </div>
                </div>
              </div>
            ))
          ) : (
            activeTrips.length === 0 && (
              <div className="card p-6 text-center py-8">
                <p className="text-sm text-slate-500 mb-3">No upcoming journeys planned.</p>
                <button onClick={() => navigate('/create-trip')} className="btn-primary text-xs mx-auto">
                  Plan Your Next Escape
                </button>
              </div>
            )
          )}
        </div>

        {/* --- SECTION 3: RECENT TRIPS --- */}
        <div>
          <div className="flex justify-between items-center mb-3">
            <h2 className="text-xl font-extrabold text-slate-900 dark:text-white">Recent Trips</h2>
            <button onClick={() => navigate('/trip-list')} className="text-xs font-bold text-primary hover:underline">View all</button>
          </div>

          {recentTrips.length > 0 ? (
            <div className="grid grid-cols-2 gap-3">
              {recentTrips.slice(0, 4).map(trip => (
                <div
                  key={trip.id || trip.name}
                  onClick={() => navigate(`/trip-details/${trip.id}`)}
                  className="card overflow-hidden group cursor-pointer hover:shadow-md transition-all"
                >
                  <div className="h-28 overflow-hidden relative">
                    <img
                      src={getDestinationImage(trip.name)}
                      alt={trip.name}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                    />
                    <span className="absolute top-2 right-2 p-1 rounded-full bg-black/30 text-white backdrop-blur-sm">
                      <ArrowUpRight size={14} />
                    </span>
                  </div>
                  <div className="p-3">
                    <h4 className="font-bold text-sm text-slate-900 dark:text-white truncate capitalize">{trip.name}</h4>
                    <p className="text-[10px] text-slate-400 font-medium truncate">{trip.dates || 'Completed trip'}</p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="card p-6 text-center text-slate-400 text-xs font-medium py-6">
              No completed trips yet. Past journeys will appear here automatically when dates pass!
            </div>
          )}
        </div>
      </div>

      {/* Bottom Navigation Bar (5 Tabs) */}
      <div className="fixed bottom-0 left-0 right-0 bg-white/90 dark:bg-navy-card/95 border-t border-slate-100 dark:border-slate-800/80 backdrop-blur-lg px-4 py-2.5 flex justify-around items-center z-50">
        <button onClick={() => navigate('/dashboard')} className="nav-pill nav-pill-active">
          <Home size={22} />
          <span className="text-[10px]">Home</span>
        </button>

        <button onClick={() => navigate('/trip-list')} className="nav-pill">
          <MapIcon size={22} />
          <span className="text-[10px]">My Trips</span>
        </button>

        <button onClick={() => navigate('/create-trip')} className="flex flex-col items-center -mt-5">
          <div className="w-12 h-12 rounded-full bg-primary text-white flex items-center justify-center shadow-lg shadow-primary/30">
            <Plus size={24} />
          </div>
          <span className="text-[10px] font-semibold text-slate-400 mt-1">Plan Trip</span>
        </button>

        <button onClick={() => navigate(allTrips[0] ? `/packing-checklist/${allTrips[0].id}` : '/trip-list')} className="nav-pill">
          <Luggage size={22} />
          <span className="text-[10px]">Packing</span>
        </button>

        <button onClick={() => navigate('/profile')} className="nav-pill">
          <User size={22} />
          <span className="text-[10px]">Profile</span>
        </button>
      </div>
    </div>
  );
};

export default DashboardScreen;
