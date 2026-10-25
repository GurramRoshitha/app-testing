import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Bell,
  Trash2,
  CheckCircle,
  Calendar,
  Plane,
  CreditCard,
  X,
  BellOff,
  Check
} from 'lucide-react';
import {
  getNotifications,
  markAsRead,
  markAllAsRead,
  deleteNotification,
  clearAllNotifications
} from '../utils/notificationService';

const NotificationScreen = () => {
  const navigate = useNavigate();
  const [notifications, setNotifications] = useState([]);
  const [showClearModal, setShowClearModal] = useState(false);

  useEffect(() => {
    setNotifications(getNotifications());
  }, []);

  const handleMarkAsRead = (id) => {
    const updated = markAsRead(id);
    setNotifications(updated);
  };

  const handleMarkAllRead = () => {
    const updated = markAllAsRead();
    setNotifications(updated);
  };

  const handleDelete = (id, e) => {
    e.stopPropagation();
    const updated = deleteNotification(id);
    setNotifications(updated);
  };

  const handleClearAll = () => {
    const updated = clearAllNotifications();
    setNotifications(updated);
    setShowClearModal(false);
  };

  const handleCardClick = (item) => {
    handleMarkAsRead(item.id);
    if (item.tripId) {
      navigate(`/trip-details/${item.tripId}`);
    }
  };

  const getIcon = (type) => {
    switch (type) {
      case 'TRIP_START':
        return <Plane size={20} className="text-primary" />;
      case 'TRIP_END':
        return <CheckCircle size={20} className="text-secondary" />;
      default:
        return <Bell size={20} className="text-primary" />;
    }
  };

  const formatTimeAgo = (timestamp) => {
    if (!timestamp) return '';
    const diffMillis = Date.now() - timestamp;
    const minutes = Math.floor(diffMillis / (1000 * 60));
    const hours = Math.floor(diffMillis / (1000 * 60 * 60));
    const days = Math.floor(diffMillis / (1000 * 60 * 60 * 24));

    if (minutes < 1) return 'Just now';
    if (minutes < 60) return `${minutes}m ago`;
    if (hours < 24) return `${hours}h ago`;
    if (days < 7) return `${days}d ago`;
    return new Date(timestamp).toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  };

  const unreadItems = notifications.filter(n => !n.isRead);
  const readItems = notifications.filter(n => n.isRead);

  return (
    <div className="min-h-screen bg-white dark:bg-background-dark pb-24">
      {/* Header */}
      <div className="px-6 py-6 flex items-center justify-between border-b border-gray-100 dark:border-surface-variant sticky top-0 bg-white/80 dark:bg-background-dark/80 backdrop-blur-md z-40">
        <div className="flex items-center gap-4">
          <button onClick={() => navigate(-1)} className="p-2 hover:bg-gray-100 dark:hover:bg-surface-variant rounded-full transition-colors">
            <ArrowLeft size={24} />
          </button>
          <h1 className="text-2xl font-black text-black dark:text-white">Notifications</h1>
        </div>

        {notifications.length > 0 && (
          <div className="flex items-center gap-2">
            <button
              onClick={handleMarkAllRead}
              className="text-xs font-bold text-primary hover:bg-primary/5 px-3 py-1.5 rounded-full transition-colors flex items-center gap-1"
            >
              <Check size={14} />
              <span>Mark Read</span>
            </button>
            <button
              onClick={() => setShowClearModal(true)}
              className="p-2 text-gray-400 hover:text-red-500 rounded-full transition-colors"
              title="Clear All"
            >
              <Trash2 size={20} />
            </button>
          </div>
        )}
      </div>

      <div className="px-6 py-6 max-w-2xl mx-auto space-y-6">
        {notifications.length === 0 ? (
          <div className="text-center py-24 flex flex-col items-center">
            <div className="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center text-primary mb-4">
              <BellOff size={32} />
            </div>
            <h3 className="text-2xl font-black text-black dark:text-white mb-2">No Notifications Yet</h3>
            <p className="text-gray-500 dark:text-gray-400 text-sm max-w-xs leading-relaxed">
              No notifications yet. Your trip updates will appear here!
            </p>
          </div>
        ) : (
          <>
            {/* Unread Section */}
            {unreadItems.length > 0 && (
              <section className="space-y-3">
                <h3 className="text-xs font-bold text-primary uppercase tracking-widest ml-1">Unread</h3>
                <div className="space-y-3">
                  {unreadItems.map(item => (
                    <div
                      key={item.id}
                      onClick={() => handleCardClick(item)}
                      className="card p-4 flex gap-4 items-start cursor-pointer border-l-4 border-l-primary hover:shadow-md transition-all relative group"
                    >
                      <div className="w-10 h-10 rounded-2xl bg-primary/10 flex items-center justify-center shrink-0">
                        {getIcon(item.type)}
                      </div>

                      <div className="flex-1 min-w-0">
                        <div className="flex justify-between items-center mb-1">
                          <h4 className="font-bold text-base text-black dark:text-white flex items-center gap-2">
                            {item.title}
                            <span className="w-2 h-2 rounded-full bg-primary inline-block"></span>
                          </h4>
                          <span className="text-[10px] text-gray-400 font-medium">
                            {formatTimeAgo(item.timestamp)}
                          </span>
                        </div>
                        <p className="text-xs text-gray-500 dark:text-gray-400 leading-relaxed">
                          {item.message}
                        </p>
                      </div>

                      <button
                        onClick={(e) => handleDelete(item.id, e)}
                        className="p-1.5 text-gray-300 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 rounded-lg transition-colors"
                        title="Delete"
                      >
                        <X size={16} />
                      </button>
                    </div>
                  ))}
                </div>
              </section>
            )}

            {/* Read / Earlier Section */}
            {readItems.length > 0 && (
              <section className="space-y-3">
                <h3 className="text-xs font-bold text-gray-400 uppercase tracking-widest ml-1">Earlier</h3>
                <div className="space-y-3">
                  {readItems.map(item => (
                    <div
                      key={item.id}
                      onClick={() => handleCardClick(item)}
                      className="card p-4 flex gap-4 items-start cursor-pointer opacity-75 hover:opacity-100 hover:shadow-md transition-all relative group bg-gray-50/50 dark:bg-surface-dark/50"
                    >
                      <div className="w-10 h-10 rounded-2xl bg-gray-100 dark:bg-surface-variant flex items-center justify-center shrink-0">
                        {getIcon(item.type)}
                      </div>

                      <div className="flex-1 min-w-0">
                        <div className="flex justify-between items-center mb-1">
                          <h4 className="font-semibold text-sm text-black dark:text-white">
                            {item.title}
                          </h4>
                          <span className="text-[10px] text-gray-400 font-medium">
                            {formatTimeAgo(item.timestamp)}
                          </span>
                        </div>
                        <p className="text-xs text-gray-500 dark:text-gray-400 leading-relaxed">
                          {item.message}
                        </p>
                      </div>

                      <button
                        onClick={(e) => handleDelete(item.id, e)}
                        className="p-1.5 text-gray-300 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 rounded-lg transition-colors"
                        title="Delete"
                      >
                        <X size={16} />
                      </button>
                    </div>
                  ))}
                </div>
              </section>
            )}
          </>
        )}
      </div>

      {/* Clear All Modal */}
      {showClearModal && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-6 bg-black/50 backdrop-blur-sm">
          <div className="bg-white dark:bg-surface-dark w-full max-w-sm rounded-[32px] p-8 shadow-2xl">
            <h2 className="text-2xl font-black text-black dark:text-white mb-4">Clear All Notifications?</h2>
            <p className="text-gray-500 dark:text-gray-400 mb-8">
              Are you sure you want to remove all notifications? This action cannot be undone.
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => setShowClearModal(false)}
                className="flex-1 px-6 py-3 rounded-2xl font-bold text-gray-500 hover:bg-gray-100 dark:hover:bg-surface-variant transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleClearAll}
                className="flex-1 px-6 py-3 rounded-2xl font-bold bg-red-500 text-white shadow-lg shadow-red-500/20 active:scale-95 transition-all"
              >
                Clear All
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationScreen;
