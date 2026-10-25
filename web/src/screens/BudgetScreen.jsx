import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Calculator,
  Plus,
  Trash2,
  X,
  Users,
  Wallet
} from 'lucide-react';
import TripService from '../data/services/TripService';

const defaultExpenses = [
  { id: '1', title: 'cab', totalCost: 3500, travelersCount: 3 },
  { id: '2', title: 'hotel', totalCost: 5500, travelersCount: 3 },
  { id: '3', title: 'food', totalCost: 4500, travelersCount: 2 }
];

const BudgetScreen = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [trip, setTrip] = useState(null);
  const [expenses, setExpenses] = useState([]);
  const [showAddModal, setShowAddModal] = useState(false);

  // Strings for fluid typing & backspacing
  const [title, setTitle] = useState('');
  const [cost, setCost] = useState('');
  const [travelersCount, setTravelersCount] = useState('2');
  const [formError, setFormError] = useState('');

  const storageKey = `voyage_group_expenses_${id || 'default'}`;

  const saveExpenses = (newExpenses) => {
    setExpenses(newExpenses);
    localStorage.setItem(storageKey, JSON.stringify(newExpenses));
  };

  useEffect(() => {
    const fetchTrip = async () => {
      if (id) {
        const result = await TripService.getTripById(id);
        if (result.isSuccess) {
          setTrip(result.trip);
          if (result.trip.members) setTravelersCount(String(result.trip.members));
        }
      }

      const saved = JSON.parse(localStorage.getItem(storageKey) || 'null');
      if (saved && Array.isArray(saved) && saved.length > 0) {
        setExpenses(saved);
      } else {
        setExpenses(defaultExpenses);
        localStorage.setItem(storageKey, JSON.stringify(defaultExpenses));
      }
    };
    fetchTrip();
  }, [id, storageKey]);

  const handleAddExpense = (e) => {
    e.preventDefault();
    setFormError('');

    const parsedCost = parseFloat(cost);
    const parsedTravelers = parseInt(travelersCount);

    if (!title.trim()) {
      setFormError('Please enter an expense title');
      return;
    }

    if (isNaN(parsedCost) || parsedCost <= 0) {
      setFormError('Total cost must be greater than 0');
      return;
    }

    if (isNaN(parsedTravelers) || parsedTravelers < 1) {
      setFormError('Number of travelers must be at least 1');
      return;
    }

    const newExpense = {
      id: `exp_${Date.now()}`,
      title: title.trim().toLowerCase(),
      totalCost: parsedCost,
      travelersCount: parsedTravelers
    };

    const updated = [newExpense, ...expenses];
    saveExpenses(updated);

    setTitle('');
    setCost('');
    setFormError('');
    setShowAddModal(false);
  };

  const handleDeleteExpense = (expId) => {
    const updated = expenses.filter(e => e.id !== expId);
    saveExpenses(updated);
  };

  const totalGroupCost = expenses.reduce((acc, curr) => acc + curr.totalCost, 0);
  const sharePerPerson = expenses.reduce((acc, curr) => {
    const share = curr.travelersCount > 0 ? curr.totalCost / curr.travelersCount : curr.totalCost;
    return acc + share;
  }, 0);

  const numCost = parseFloat(cost) || 0;
  const numTravelers = parseInt(travelersCount) || 1;
  const calculatedShare = numTravelers > 0 ? numCost / numTravelers : numCost;

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-navy pb-24 text-slate-800 dark:text-slate-100">
      {/* Top Bar */}
      <div className="px-6 py-4 flex items-center justify-between sticky top-0 bg-white/90 dark:bg-navy-card/90 backdrop-blur-md z-40 border-b border-slate-100 dark:border-slate-800">
        <div className="flex items-center gap-4">
          <button onClick={() => navigate(-1)} className="p-2 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-full transition-colors">
            <ArrowLeft size={22} />
          </button>
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-2xl bg-mint dark:bg-mint-dark text-primary flex items-center justify-center">
              <Calculator size={20} />
            </div>
            <div>
              <h1 className="text-xl font-black text-slate-900 dark:text-white leading-tight">Group Cost Split</h1>
              <p className="text-xs text-slate-400 font-medium">Split shared expenses equally among travelers</p>
            </div>
          </div>
        </div>

        <button
          onClick={() => {
            setFormError('');
            setShowAddModal(true);
          }}
          className="btn-primary py-2.5 px-4 text-xs shadow-md"
        >
          <Plus size={16} />
          <span>Add Expense</span>
        </button>
      </div>

      <div className="px-6 py-6 max-w-2xl mx-auto space-y-6">
        {/* Summary Cards */}
        <div className="card p-6 bg-white dark:bg-navy-card border border-slate-200 dark:border-slate-800 shadow-sm grid grid-cols-2 gap-4">
          <div>
            <span className="text-[10px] font-black tracking-widest text-slate-400 uppercase block mb-1">
              TOTAL GROUP COST
            </span>
            <h3 className="text-2xl font-black text-slate-900 dark:text-white">
              ₹{totalGroupCost.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </h3>
          </div>

          <div>
            <span className="text-[10px] font-black tracking-widest text-primary uppercase block mb-1">
              SHARE PER PERSON
            </span>
            <h3 className="text-2xl font-black text-primary">
              ₹{sharePerPerson.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </h3>
          </div>
        </div>

        {/* Expenses List */}
        <div className="space-y-3">
          {expenses.length === 0 ? (
            <div className="card p-8 text-center text-slate-400 text-xs font-medium py-12">
              No shared expenses added yet. Click '+ Add Expense' to start splitting group costs!
            </div>
          ) : (
            expenses.map(exp => {
              const perPersonShare = exp.travelersCount > 0 ? exp.totalCost / exp.travelersCount : exp.totalCost;

              return (
                <div
                  key={exp.id}
                  className="card p-4 flex justify-between items-center hover:shadow-md transition-shadow border-slate-200 dark:border-slate-800"
                >
                  <div>
                    <h4 className="font-extrabold text-base text-slate-900 dark:text-white capitalize">
                      {exp.title}
                    </h4>
                    <p className="text-xs text-slate-400 font-medium mt-0.5">
                      Total: ₹{exp.totalCost.toLocaleString('en-IN', { minimumFractionDigits: 2 })} &bull; {exp.travelersCount} Travelers
                    </p>
                  </div>

                  <div className="flex items-center gap-4">
                    <div className="text-right">
                      <span className="text-base font-black text-primary block">
                        ₹{perPersonShare.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                      </span>
                      <span className="text-[9px] font-extrabold text-slate-400 uppercase block">
                        PER PERSON
                      </span>
                    </div>

                    <button
                      onClick={() => handleDeleteExpense(exp.id)}
                      className="p-2 text-slate-300 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-950/20 rounded-xl transition-colors"
                      title="Delete Expense"
                    >
                      <Trash2 size={18} />
                    </button>
                  </div>
                </div>
              );
            })
          )}
        </div>
      </div>

      {/* Add Expense Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-6 bg-black/50 backdrop-blur-sm">
          <div className="bg-white dark:bg-navy-card w-full max-w-md rounded-[32px] p-8 shadow-2xl relative border border-slate-200 dark:border-slate-800">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-black text-slate-900 dark:text-white">Split Shared Expense</h2>
              <button
                onClick={() => setShowAddModal(false)}
                className="p-2 text-slate-400 hover:text-slate-900 dark:hover:text-white rounded-full"
              >
                <X size={20} />
              </button>
            </div>

            {formError && (
              <div className="mb-4 p-3 bg-red-50 text-red-600 rounded-xl text-xs font-bold">
                {formError}
              </div>
            )}

            <form onSubmit={handleAddExpense} className="space-y-5">
              <div>
                <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2 ml-1">
                  Expense Description
                </label>
                <input
                  type="text"
                  autoFocus
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="e.g. Hotel Booking, Dinner, Cab"
                  className="w-full bg-slate-50 dark:bg-slate-800 border-2 border-slate-200 dark:border-slate-700 rounded-2xl px-4 py-3 text-slate-900 dark:text-white outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all font-medium text-base z-50 pointer-events-auto relative cursor-text"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2 ml-1">
                  Total Shared Amount (₹)
                </label>
                <input
                  type="number"
                  step="any"
                  value={cost}
                  onChange={(e) => setCost(e.target.value)}
                  placeholder="e.g. 2400"
                  className="w-full bg-slate-50 dark:bg-slate-800 border-2 border-slate-200 dark:border-slate-700 rounded-2xl px-4 py-3 text-slate-900 dark:text-white outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all font-medium text-base z-50 pointer-events-auto relative cursor-text"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2 ml-1">
                  Number of Travelers
                </label>
                <input
                  type="number"
                  min="1"
                  value={travelersCount}
                  onChange={(e) => setTravelersCount(e.target.value)}
                  className="w-full bg-slate-50 dark:bg-slate-800 border-2 border-slate-200 dark:border-slate-700 rounded-2xl px-4 py-3 text-slate-900 dark:text-white outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all font-medium text-base z-50 pointer-events-auto relative cursor-text"
                  required
                />
              </div>

              {/* Live Calculation Preview */}
              {numCost > 0 && numTravelers >= 1 && (
                <div className="p-4 bg-primary/5 rounded-2xl border border-primary/20 space-y-1">
                  <div className="flex justify-between text-xs text-slate-500">
                    <span>Total Expense:</span>
                    <span className="font-bold">₹{numCost.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between text-xs text-slate-500">
                    <span>Travelers:</span>
                    <span className="font-bold">{numTravelers}</span>
                  </div>
                  <div className="flex justify-between text-sm font-black text-primary pt-2 border-t border-primary/10">
                    <span>Amount Per Person:</span>
                    <span>₹{calculatedShare.toFixed(2)}</span>
                  </div>
                </div>
              )}

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="flex-1 px-6 py-3.5 rounded-2xl font-bold text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 btn-primary py-3.5 shadow-lg shadow-primary/20"
                >
                  Add Expense
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default BudgetScreen;
