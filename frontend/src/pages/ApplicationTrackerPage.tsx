import React, { useEffect, useState } from 'react';
import { apiClient } from '../api/client';
import type { ApiResponse } from '../types';
import {
  Plus,
  Trash2,
  FileCheck,
} from 'lucide-react';

const STATUS_COLUMNS = [
  { key: 'SAVED', label: 'Saved', color: 'border-slate-700 bg-slate-900/30' },
  { key: 'PREPARING', label: 'Preparing', color: 'border-amber-900/50 bg-amber-950/20' },
  { key: 'APPLIED', label: 'Applied', color: 'border-blue-900/50 bg-blue-950/20' },
  { key: 'INTERVIEW', label: 'Interview', color: 'border-indigo-900/50 bg-indigo-950/20' },
  { key: 'OFFER', label: 'Offer', color: 'border-emerald-900/50 bg-emerald-950/20' },
  { key: 'REJECTED', label: 'Archived', color: 'border-slate-800 bg-surface-950/30' },
];

export const ApplicationTrackerPage: React.FC = () => {
  const [applications, setApplications] = useState<any[]>([]);
  const [jobs, setJobs] = useState<any[]>([]);
  const [analytics, setAnalytics] = useState<any | null>(null);
  const [showAddModal, setShowAddModal] = useState(false);

  const [newApp, setNewApp] = useState({
    jobId: '',
    status: 'SAVED',
    notes: '',
  });

  const fetchData = async () => {
    try {
      const [appRes, jobRes, anRes] = await Promise.all([
        apiClient.get<ApiResponse<any[]>>('/applications'),
        apiClient.get<ApiResponse<any[]>>('/jobs'),
        apiClient.get<ApiResponse<any>>('/applications/analytics').catch(() => null),
      ]);

      if (appRes.data.success) setApplications(appRes.data.data);
      if (jobRes.data.success) setJobs(jobRes.data.data);
      if (anRes?.data?.success) setAnalytics(anRes.data.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleStatusChange = async (id: string, newStatus: string) => {
    try {
      await apiClient.patch(`/applications/${id}/status`, { status: newStatus });
      fetchData();
    } catch (err) {
      console.error(err);
    }
  };

  const handleCreateApplication = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newApp.jobId) {
      alert('Please select a target job.');
      return;
    }
    try {
      await apiClient.post('/applications', newApp);
      setShowAddModal(false);
      setNewApp({ jobId: '', status: 'SAVED', notes: '' });
      fetchData();
    } catch (err: any) {
      alert(err?.response?.data?.error?.details || 'Failed to create application');
    }
  };

  const handleDeleteApplication = async (id: string) => {
    if (!confirm('Are you sure you want to remove this tracked application?')) return;
    try {
      await apiClient.delete(`/applications/${id}`);
      fetchData();
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div>
      {/* Pagehead */}
      <div className="pagehead">
        <div>
          <div className="eyebrow">YOUR APPLICATION PIPELINE</div>
          <h1>
            Job application tracker <span style={{ fontSize: '24px' }}>✦</span>
          </h1>
          <p>Track your job search across every interview stage with linked tailored resumes.</p>
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="btn primary"
        >
          <Plus className="h-4 w-4" />
          <span>＋ Track New Application</span>
        </button>
      </div>

      {/* Metrics Bar */}
      <div className="grid stats">
        <div className="card">
          <div className="stat-top">
            <span>Total Tracked</span>
            <span className="stat-icon">▤</span>
          </div>
          <div className="stat-num">{applications.length}</div>
          <div className="stat-foot">Tracked vacancies</div>
        </div>

        <div className="card">
          <div className="stat-top">
            <span>Active Pipeline</span>
            <span className="stat-icon">⏳</span>
          </div>
          <div className="stat-num">{analytics?.activeApplications ?? 0}</div>
          <div className="stat-foot">In active stages</div>
        </div>

        <div className="card">
          <div className="stat-top">
            <span>Interviews</span>
            <span className="stat-icon">✦</span>
          </div>
          <div className="stat-num">{analytics?.interviewsScheduled ?? 0}</div>
          <div className="stat-foot">Scheduled discussions</div>
        </div>

        <div className="card">
          <div className="stat-top">
            <span>Offers</span>
            <span className="stat-icon">✓</span>
          </div>
          <div className="stat-num">{analytics?.offersReceived ?? 0}</div>
          <div className="stat-foot">Offers received</div>
        </div>
      </div>

      {/* Kanban Board Columns */}
      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-6 gap-4 items-start overflow-x-auto pb-4">
        {STATUS_COLUMNS.map((col) => {
          const colApps = applications.filter((a) => {
            if (col.key === 'REJECTED') {
              return a.status === 'REJECTED' || a.status === 'WITHDRAWN';
            }
            return a.status === col.key;
          });

          return (
            <div
              key={col.key}
              className={`rounded-2xl border p-3 flex flex-col min-h-[500px] ${col.color}`}
            >
              <div className="flex items-center justify-between mb-3 px-1">
                <span className="text-xs font-bold uppercase tracking-wider text-slate-200">
                  {col.label}
                </span>
                <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-surface-800 text-slate-300">
                  {colApps.length}
                </span>
              </div>

              <div className="space-y-2.5 flex-1">
                {colApps.map((app) => (
                  <div
                    key={app.id}
                    className="rounded-xl border border-surface-800/90 bg-surface-950/80 p-3.5 shadow-sm space-y-2 hover:border-surface-700 transition-all group"
                  >
                    <div className="flex items-start justify-between gap-1">
                      <div>
                        <h4 className="text-xs font-bold text-white line-clamp-1">{app.jobTitle}</h4>
                        <p className="text-[11px] font-semibold text-brand-400">{app.company}</p>
                      </div>
                      <button
                        onClick={() => handleDeleteApplication(app.id)}
                        className="text-slate-600 hover:text-rose-400 p-0.5 transition-colors"
                        title="Delete"
                      >
                        <Trash2 className="h-3 w-3" />
                      </button>
                    </div>

                    {app.resumeVersionNumber && (
                      <div className="flex items-center gap-1 text-[10px] text-slate-400">
                        <FileCheck className="h-3 w-3 text-emerald-400" />
                        <span>Resume v{app.resumeVersionNumber} linked</span>
                      </div>
                    )}

                    {app.notes && (
                      <p className="text-[11px] text-slate-400 line-clamp-2 italic bg-surface-900/60 p-1.5 rounded-lg border border-surface-800">
                        "{app.notes}"
                      </p>
                    )}

                    {/* Quick Move Dropdown */}
                    <div className="pt-2 border-t border-surface-800 flex items-center justify-between">
                      <span className="text-[10px] text-slate-500">Stage:</span>
                      <select
                        value={app.status}
                        onChange={(e) => handleStatusChange(app.id, e.target.value)}
                        className="text-[10px] bg-surface-900 text-slate-300 border border-surface-700 rounded-md px-1.5 py-0.5 focus:outline-none"
                      >
                        <option value="SAVED">Saved</option>
                        <option value="PREPARING">Preparing</option>
                        <option value="APPLIED">Applied</option>
                        <option value="INTERVIEW">Interview</option>
                        <option value="OFFER">Offer</option>
                        <option value="REJECTED">Rejected</option>
                      </select>
                    </div>
                  </div>
                ))}

                {colApps.length === 0 && (
                  <div className="h-24 flex items-center justify-center border border-dashed border-surface-800/80 rounded-xl text-[11px] text-slate-600">
                    Empty
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>

      {/* MODAL: Add Application */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 backdrop-blur-sm p-4">
          <div className="w-full max-w-md rounded-2xl border border-surface-700 bg-surface-900 p-6 shadow-2xl">
            <h3 className="text-lg font-bold text-white mb-4">Track Job Application</h3>
            <form onSubmit={handleCreateApplication} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Select Target Vacancy</label>
                <select
                  required
                  value={newApp.jobId}
                  onChange={(e) => setNewApp({ ...newApp, jobId: e.target.value })}
                  className="w-full rounded-xl border border-surface-700 bg-surface-950 p-2.5 text-xs text-white"
                >
                  <option value="">-- Choose Vacancy --</option>
                  {jobs.map((j) => (
                    <option key={j.id} value={j.id}>
                      {j.company} — {j.jobTitle}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Pipeline Stage</label>
                <select
                  value={newApp.status}
                  onChange={(e) => setNewApp({ ...newApp, status: e.target.value })}
                  className="w-full rounded-xl border border-surface-700 bg-surface-950 p-2.5 text-xs text-white"
                >
                  <option value="SAVED">Saved</option>
                  <option value="PREPARING">Preparing</option>
                  <option value="APPLIED">Applied</option>
                  <option value="INTERVIEW">Interview</option>
                  <option value="OFFER">Offer</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Interview / Application Notes</label>
                <textarea
                  rows={3}
                  value={newApp.notes}
                  onChange={(e) => setNewApp({ ...newApp, notes: e.target.value })}
                  placeholder="e.g. Applied via referral, hiring manager call on Tuesday..."
                  className="w-full rounded-xl border border-surface-700 bg-surface-950 p-2.5 text-xs text-white"
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="rounded-xl px-4 py-2 text-xs font-medium text-slate-400 hover:text-white"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="rounded-xl bg-brand-600 px-5 py-2 text-xs font-semibold text-white hover:bg-brand-500 shadow-md shadow-brand-600/30"
                >
                  Save Application
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
