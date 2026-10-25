import React, { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  User,
  Map as MapIcon,
  LogOut,
  ChevronRight,
  ArrowLeft,
  Settings,
  Bell,
  Camera,
  Phone,
  Mail,
  X
} from 'lucide-react';
import AuthService from '../data/services/AuthService';

const ProfileScreen = () => {
  const navigate = useNavigate();
  const fileInputRef = useRef(null);

  const [user, setUser] = useState(null);
  const [profile, setProfile] = useState({
    fullName: '',
    email: '',
    phoneNumber: '',
    photoUri: ''
  });

  const [showEditModal, setShowEditModal] = useState(false);
  const [editName, setEditName] = useState('');
  const [editPhone, setEditPhone] = useState('');
  const [editPhoto, setEditPhoto] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    const currentUser = AuthService.getCurrentUser();
    if (!currentUser) {
      navigate('/login');
      return;
    }
    setUser(currentUser);

    const savedProfile = JSON.parse(localStorage.getItem(`voyage_profile_${currentUser.uid}`) || '{}');
    const fullProfile = {
      fullName: savedProfile.fullName || currentUser.displayName || 'Voyage Explorer',
      email: currentUser.email || 'explorer@voyage.ai',
      phoneNumber: savedProfile.phoneNumber || '',
      photoUri: savedProfile.photoUri || currentUser.photoURL || ''
    };

    setProfile(fullProfile);
    setEditName(fullProfile.fullName);
    setEditPhone(fullProfile.phoneNumber);
    setEditPhoto(fullProfile.photoUri);
  }, [navigate]);

  const handleLogout = async () => {
    await AuthService.logout();
    navigate('/login');
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setEditPhoto(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSave = (e) => {
    e.preventDefault();
    setErrorMessage('');

    if (!editName.trim()) {
      setErrorMessage('Full Name cannot be empty');
      return;
    }

    if (editPhone.trim()) {
      const cleanPhone = editPhone.trim();
      if (!/^[0-9+\s-]{7,15}$/.test(cleanPhone)) {
        setErrorMessage('Please enter a valid phone number');
        return;
      }
    }

    const updated = {
      ...profile,
      fullName: editName.trim(),
      phoneNumber: editPhone.trim(),
      photoUri: editPhoto
    };

    setProfile(updated);
    if (user?.uid) {
      localStorage.setItem(`voyage_profile_${user.uid}`, JSON.stringify(updated));
    }
    setShowEditModal(false);
  };

  const ProfileOption = ({ icon: IconComponent, label, onClick, isError = false }) => (
    <button
      onClick={onClick}
      className="w-full flex items-center justify-between p-5 card border-none hover:bg-gray-50 dark:hover:bg-surface-variant/30 transition-colors mb-4"
    >
      <div className="flex items-center gap-4">
        <div className={`p-2.5 rounded-2xl ${isError ? 'bg-red-50 text-red-500' : 'bg-primary/5 text-primary'}`}>
          <IconComponent size={22} />
        </div>
        <span className={`font-bold ${isError ? 'text-red-500' : 'text-black dark:text-white'}`}>{label}</span>
      </div>
      <ChevronRight size={20} className="text-gray-300" />
    </button>
  );

  return (
    <div className="min-h-screen bg-white dark:bg-background-dark pb-24">
      {/* Header */}
      <div className="px-6 py-8 flex items-center gap-4">
        <button onClick={() => navigate('/dashboard')} className="p-2 hover:bg-gray-100 dark:hover:bg-surface-variant rounded-full transition-colors">
          <ArrowLeft size={24} />
        </button>
        <h1 className="text-3xl font-black text-black dark:text-white">Profile</h1>
      </div>

      <div className="px-6 max-w-xl mx-auto">
        {/* Profile Card */}
        <div className="flex flex-col items-center py-10 mb-10">
          <div className="relative mb-6">
            <div className="w-32 h-32 rounded-[40px] bg-gradient-to-tr from-primary to-secondary flex items-center justify-center text-white shadow-xl shadow-primary/20 overflow-hidden">
              {profile.photoUri ? (
                <img src={profile.photoUri} alt="Profile" className="w-full h-full object-cover" />
              ) : (
                <User size={64} strokeWidth={1.5} />
              )}
            </div>

            <button
              onClick={() => {
                setEditName(profile.fullName);
                setEditPhone(profile.phoneNumber);
                setEditPhoto(profile.photoUri);
                setShowEditModal(true);
              }}
              className="absolute -bottom-2 -right-2 w-10 h-10 rounded-2xl bg-primary text-white flex items-center justify-center shadow-lg hover:bg-primary/90 transition-colors"
              title="Change Photo"
            >
              <Camera size={18} />
            </button>
          </div>

          <h2 className="text-3xl font-black text-black dark:text-white mb-1 tracking-tight text-center">
            {profile.fullName || 'Voyage Explorer'}
          </h2>
          <p className="text-gray-500 font-medium mb-1">{profile.email}</p>

          {profile.phoneNumber && (
            <div className="flex items-center gap-1.5 text-xs text-primary font-bold mt-1">
              <Phone size={14} />
              <span>{profile.phoneNumber}</span>
            </div>
          )}

          <button
            onClick={() => {
              setEditName(profile.fullName);
              setEditPhone(profile.phoneNumber);
              setEditPhoto(profile.photoUri);
              setShowEditModal(true);
            }}
            className="btn-primary mt-8 px-8 py-3 text-sm shadow-md shadow-primary/20"
          >
            Edit Profile
          </button>
        </div>

        {/* Options */}
        <div className="space-y-2">
          <h3 className="text-xs font-bold text-gray-400 uppercase tracking-widest ml-1 mb-4">Journey Management</h3>
          <ProfileOption icon={MapIcon} label="My Trips" onClick={() => navigate('/trip-list')} />

          <h3 className="text-xs font-bold text-gray-400 uppercase tracking-widest ml-1 mt-8 mb-4">Account Settings</h3>
          <ProfileOption icon={Bell} label="Notifications Center" onClick={() => navigate('/notifications')} />
          <ProfileOption icon={Settings} label="App Settings" onClick={() => navigate('/settings')} />

          <div className="pt-6">
            <ProfileOption icon={LogOut} label="Logout" onClick={handleLogout} isError={true} />
          </div>
        </div>
      </div>

      {/* Edit Profile Modal */}
      {showEditModal && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-6 bg-black/50 backdrop-blur-sm">
          <div className="bg-white dark:bg-surface-dark w-full max-w-md rounded-[32px] p-8 shadow-2xl relative">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-black text-black dark:text-white">Edit Profile</h2>
              <button
                onClick={() => setShowEditModal(false)}
                className="p-2 text-gray-400 hover:text-black dark:hover:text-white rounded-full"
              >
                <X size={20} />
              </button>
            </div>

            {errorMessage && (
              <div className="mb-4 p-3 bg-red-50 text-red-600 rounded-xl text-xs font-bold">
                {errorMessage}
              </div>
            )}

            <form onSubmit={handleSave} className="space-y-5">
              {/* Profile Photo Selector */}
              <div className="flex flex-col items-center mb-2">
                <div className="relative cursor-pointer" onClick={() => fileInputRef.current?.click()}>
                  <div className="w-24 h-24 rounded-3xl bg-primary/10 flex items-center justify-center text-primary overflow-hidden border-2 border-primary/20">
                    {editPhoto ? (
                      <img src={editPhoto} alt="Preview" className="w-full h-full object-cover" />
                    ) : (
                      <User size={40} />
                    )}
                  </div>
                  <div className="absolute -bottom-1 -right-1 w-8 h-8 rounded-xl bg-primary text-white flex items-center justify-center shadow-md">
                    <Camera size={14} />
                  </div>
                </div>
                <input
                  type="file"
                  ref={fileInputRef}
                  onChange={handleFileChange}
                  accept="image/*"
                  className="hidden"
                />
                <button
                  type="button"
                  onClick={() => fileInputRef.current?.click()}
                  className="text-xs font-bold text-primary mt-2 hover:underline"
                >
                  Change Profile Picture
                </button>
              </div>

              <div>
                <label className="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2 ml-1">
                  Full Name
                </label>
                <input
                  type="text"
                  value={editName}
                  onChange={(e) => setEditName(e.target.value)}
                  className="input-field"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2 ml-1">
                  Email Address (Read-Only)
                </label>
                <input
                  type="email"
                  value={profile.email}
                  disabled
                  className="input-field bg-gray-100 dark:bg-surface-variant/30 text-gray-500 cursor-not-allowed"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2 ml-1">
                  Phone Number
                </label>
                <input
                  type="tel"
                  value={editPhone}
                  onChange={(e) => setEditPhone(e.target.value)}
                  placeholder="e.g. +1 234 567 8900"
                  className="input-field"
                />
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowEditModal(false)}
                  className="flex-1 px-6 py-3.5 rounded-2xl font-bold text-gray-500 hover:bg-gray-100 dark:hover:bg-surface-variant transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 btn-primary py-3.5 shadow-lg shadow-primary/20"
                >
                  Save Changes
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProfileScreen;
