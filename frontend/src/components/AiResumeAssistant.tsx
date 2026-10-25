import React, { useState, useRef, useEffect } from 'react';
import { Sparkles, X, Send, Copy, Check, Mail, BookOpen, Wand2 } from 'lucide-react';
import { apiClient } from '../api/client';
import type { ApiResponse, AiAssistantRequest, AiAssistantResponse } from '../types';

interface Message {
  id: string;
  sender: 'user' | 'assistant';
  text: string;
  emailSubject?: string;
  emailBody?: string;
  modifiedSummary?: string;
  suggestedSkills?: string[];
  missingSkills?: string[];
}

interface AiResumeAssistantProps {
  targetJobId?: string;
  currentSummary?: string;
  onApplySummary?: (summary: string) => void;
}

export const AiResumeAssistant: React.FC<AiResumeAssistantProps> = ({
  targetJobId,
  currentSummary,
  onApplySummary,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [copiedId, setCopiedId] = useState<string | null>(null);
  const [emailTone, setEmailTone] = useState<'professional' | 'concise' | 'warm'>('professional');

  const [messages, setMessages] = useState<Message[]>([
    {
      id: 'init-1',
      sender: 'assistant',
      text: 'Hi! I am your AI Resume Assistant. I can help shorten your summary, polish bullet points, analyze target job skill gaps, or draft an application email based strictly on your verified background.',
    },
  ]);

  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    if (isOpen) {
      scrollToBottom();
    }
  }, [messages, isOpen]);

  const handleSend = async (customPrompt?: string, action?: AiAssistantRequest['action']) => {
    const promptText = (customPrompt || input).trim();
    if (!promptText) return;

    const userMsgId = 'user-' + Date.now();
    const newMsg: Message = { id: userMsgId, sender: 'user', text: promptText };
    setMessages((prev) => [...prev, newMsg]);
    if (!customPrompt) setInput('');
    setIsLoading(true);

    try {
      const payload: AiAssistantRequest = {
        prompt: promptText,
        targetJobId,
        currentSummary,
        tone: emailTone,
        action: action || 'GENERAL',
      };

      const res = await apiClient.post<ApiResponse<AiAssistantResponse>>('/resume/assistant', payload);
      const data = res.data.data;

      const botMsg: Message = {
        id: 'bot-' + Date.now(),
        sender: 'assistant',
        text: data.reply || 'Here is what I found based on your verified credentials.',
        emailSubject: data.emailSubject,
        emailBody: data.emailBody,
        modifiedSummary: data.modifiedSummary,
        suggestedSkills: data.suggestedSkills,
        missingSkills: data.missingSkills,
      };

      setMessages((prev) => [...prev, botMsg]);
    } catch {
      setMessages((prev) => [
        ...prev,
        {
          id: 'bot-err-' + Date.now(),
          sender: 'assistant',
          text: 'Unable to reach the assistant service at this moment. Please verify your connection.',
        },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCopy = (text: string, id: string) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  return (
    <>
      {/* Floating Action Button */}
      {!isOpen && (
        <button
          onClick={() => setIsOpen(true)}
          className="fixed bottom-5 right-5 z-40 inline-flex items-center gap-2 rounded-full bg-brand-600 px-4 py-3 text-xs sm:text-sm font-bold text-white shadow-2xl shadow-brand-600/30 hover:scale-105 active:scale-95 transition-all"
          aria-label="Open AI Resume Assistant"
        >
          <Sparkles className="h-4 w-4" />
          <span>AI Resume Assistant</span>
        </button>
      )}

      {/* Assistant Modal / Panel */}
      {isOpen && (
        <div className="fixed bottom-4 right-4 sm:bottom-6 sm:right-6 z-50 flex flex-col w-[calc(100vw-32px)] sm:w-[400px] h-[520px] max-h-[85vh] rounded-2xl border border-slate-200 dark:border-surface-800 bg-white dark:bg-[#111119] shadow-2xl overflow-hidden transition-all animate-in fade-in slide-in-from-bottom-4 duration-200">
          {/* Header */}
          <div className="flex items-center justify-between px-4 py-3 border-b border-slate-200 dark:border-surface-800 bg-slate-50/80 dark:bg-surface-950/80">
            <div className="flex items-center gap-2.5">
              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-brand-600 text-white font-bold shadow-md shadow-brand-600/20">
                <Sparkles className="h-4 w-4" />
              </div>
              <div>
                <h3 className="text-xs sm:text-sm font-bold text-slate-900 dark:text-white leading-tight">
                  Resume Assistant
                </h3>
                <p className="text-[10px] text-slate-500 dark:text-slate-400">
                  Strictly truth-preserving • Explainable AI
                </p>
              </div>
            </div>
            <button
              onClick={() => setIsOpen(false)}
              className="p-1 rounded-lg text-slate-400 hover:text-slate-700 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-surface-800 transition-colors"
              aria-label="Close assistant"
            >
              <X className="h-4 w-4" />
            </button>
          </div>

          {/* Messages Stream */}
          <div className="flex-1 overflow-y-auto p-4 space-y-3.5 text-xs">
            {messages.map((m) => (
              <div
                key={m.id}
                className={`flex flex-col ${m.sender === 'user' ? 'items-end' : 'items-start'}`}
              >
                <div
                  className={`max-w-[85%] rounded-2xl px-3.5 py-2.5 leading-relaxed ${
                    m.sender === 'user'
                      ? 'bg-brand-600 text-white font-medium rounded-tr-none'
                      : 'bg-slate-100 dark:bg-surface-950 text-slate-800 dark:text-slate-200 border border-slate-200/80 dark:border-surface-800/80 rounded-tl-none'
                  }`}
                >
                  <p className="whitespace-pre-wrap">{m.text}</p>

                  {/* Summary Replacement Preview */}
                  {m.modifiedSummary && (
                    <div className="mt-2.5 pt-2 border-t border-slate-200 dark:border-surface-800">
                      <p className="text-[11px] font-semibold text-brand-600 dark:text-brand-400 mb-1">
                        Suggested Summary:
                      </p>
                      <p className="italic text-[11px] text-slate-700 dark:text-slate-300">
                        "{m.modifiedSummary}"
                      </p>
                      {onApplySummary && (
                        <button
                          onClick={() => onApplySummary(m.modifiedSummary!)}
                          className="mt-2 inline-flex items-center gap-1 px-2.5 py-1 rounded bg-brand-600 text-[10px] font-semibold text-white hover:bg-brand-500"
                        >
                          <Wand2 className="h-3 w-3" />
                          <span>Apply to Resume Preview</span>
                        </button>
                      )}
                    </div>
                  )}

                  {/* Email Draft Preview */}
                  {m.emailBody && (
                    <div className="mt-3 p-2.5 rounded-xl border border-slate-300 dark:border-surface-700 bg-white dark:bg-surface-900 text-slate-800 dark:text-slate-200 space-y-1.5">
                      <div className="flex items-center justify-between">
                        <span className="text-[10px] font-bold text-brand-600 dark:text-brand-400 uppercase tracking-wider">
                          Email Draft
                        </span>
                        <button
                          onClick={() => handleCopy(`${m.emailSubject}\n\n${m.emailBody}`, m.id)}
                          className="inline-flex items-center gap-1 text-[10px] font-semibold text-slate-500 hover:text-brand-600 dark:hover:text-brand-400"
                        >
                          {copiedId === m.id ? (
                            <>
                              <Check className="h-3 w-3 text-emerald-500" />
                              <span className="text-emerald-500">Copied</span>
                            </>
                          ) : (
                            <>
                              <Copy className="h-3 w-3" />
                              <span>Copy Email</span>
                            </>
                          )}
                        </button>
                      </div>
                      <div className="text-[11px] font-semibold">
                        Subject: <span className="font-normal">{m.emailSubject}</span>
                      </div>
                      <div className="text-[10px] whitespace-pre-line text-slate-600 dark:text-slate-300 leading-relaxed max-h-48 overflow-y-auto">
                        {m.emailBody}
                      </div>
                    </div>
                  )}

                  {/* Skill Gaps Breakdown */}
                  {m.missingSkills && m.missingSkills.length > 0 && (
                    <div className="mt-2.5 pt-2 border-t border-slate-200 dark:border-surface-800">
                      <span className="text-[10px] font-bold text-amber-500 uppercase tracking-wider block mb-1">
                        Missing from Verified Profile:
                      </span>
                      <div className="flex flex-wrap gap-1">
                        {m.missingSkills.map((sk, idx) => (
                          <span
                            key={idx}
                            className="px-2 py-0.5 rounded text-[10px] bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/20"
                          >
                            {sk}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            ))}
            {isLoading && (
              <div className="flex items-center gap-2 text-xs text-slate-400 italic">
                <Sparkles className="h-3.5 w-3.5 animate-spin text-brand-500" />
                <span>Assistant is drafting response...</span>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Quick Action Suggestions */}
          <div className="px-3 py-2 bg-slate-50 dark:bg-surface-950/60 border-t border-slate-200 dark:border-surface-800">
            <div className="flex gap-1.5 overflow-x-auto pb-1 no-scrollbar">
              <button
                onClick={() => handleSend('Make my summary concise', 'SHORTEN_SUMMARY')}
                className="whitespace-nowrap px-2.5 py-1 rounded-full text-[10px] font-medium border border-slate-200 dark:border-surface-700 bg-white dark:bg-surface-900 text-slate-700 dark:text-slate-300 hover:border-brand-500 transition-colors"
              >
                ✂ Shorten summary
              </button>
              <button
                onClick={() => handleSend('Make the wording more professional', 'POLISH_SUMMARY')}
                className="whitespace-nowrap px-2.5 py-1 rounded-full text-[10px] font-medium border border-slate-200 dark:border-surface-700 bg-white dark:bg-surface-900 text-slate-700 dark:text-slate-300 hover:border-brand-500 transition-colors"
              >
                ✦ Polish wording
              </button>
              <button
                onClick={() => handleSend('What skills are missing for this vacancy?', 'GAP_ANALYSIS')}
                className="whitespace-nowrap px-2.5 py-1 rounded-full text-[10px] font-medium border border-slate-200 dark:border-surface-700 bg-white dark:bg-surface-900 text-slate-700 dark:text-slate-300 hover:border-brand-500 transition-colors"
              >
                <BookOpen className="h-3 w-3 inline mr-1" />
                Skills gap
              </button>
              <button
                onClick={() => handleSend('Draft an application email for this role', 'DRAFT_EMAIL')}
                className="whitespace-nowrap px-2.5 py-1 rounded-full text-[10px] font-medium border border-slate-200 dark:border-surface-700 bg-white dark:bg-surface-900 text-slate-700 dark:text-slate-300 hover:border-brand-500 transition-colors"
              >
                <Mail className="h-3 w-3 inline mr-1" />
                Draft email
              </button>
            </div>

            {/* Email Tone Selector */}
            <div className="flex items-center justify-between pt-1 text-[10px] text-slate-500">
              <span>Email Tone:</span>
              <div className="flex gap-1">
                {(['professional', 'concise', 'warm'] as const).map((t) => (
                  <button
                    key={t}
                    onClick={() => setEmailTone(t)}
                    className={`px-1.5 py-0.5 rounded capitalize ${
                      emailTone === t
                        ? 'bg-brand-600 text-white font-semibold'
                        : 'text-slate-500 hover:text-slate-900 dark:hover:text-slate-200'
                    }`}
                  >
                    {t}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* Input Form */}
          <form
            onSubmit={(e) => {
              e.preventDefault();
              handleSend();
            }}
            className="flex items-center gap-2 p-3 border-t border-slate-200 dark:border-surface-800 bg-white dark:bg-[#111119]"
          >
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Ask for a resume change..."
              disabled={isLoading}
              className="flex-1 rounded-xl border border-slate-200 dark:border-surface-700 bg-slate-50 dark:bg-surface-950 px-3 py-2 text-xs text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:border-brand-500"
            />
            <button
              type="submit"
              disabled={isLoading || !input.trim()}
              className="flex h-8 w-8 items-center justify-center rounded-xl bg-brand-600 text-white hover:bg-brand-500 disabled:opacity-50 transition-all flex-shrink-0"
            >
              <Send className="h-3.5 w-3.5" />
            </button>
          </form>
        </div>
      )}
    </>
  );
};
