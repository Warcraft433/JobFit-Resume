import React, { createContext, useContext, useState, useEffect } from 'react';
import { apiClient } from '../api/client';
import type { User, AuthResponse, ApiResponse } from '../types';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (fullName: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('jobfit_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('jobfit_token'));
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const refreshUser = async () => {
    try {
      if (!localStorage.getItem('jobfit_token')) {
        setUser(null);
        return;
      }
      const res = await apiClient.get<ApiResponse<User>>('/auth/me');
      if (res.data.success && res.data.data) {
        setUser(res.data.data);
        localStorage.setItem('jobfit_user', JSON.stringify(res.data.data));
      }
    } catch {
      logout();
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (token) {
      refreshUser();
    } else {
      setIsLoading(false);
    }
  }, [token]);

  const login = async (email: string, password: string) => {
    // Purge any previous user session before authenticating
    localStorage.removeItem('jobfit_token');
    localStorage.removeItem('jobfit_user');
    setToken(null);
    setUser(null);

    const res = await apiClient.post<ApiResponse<AuthResponse>>('/auth/login', { email, password });
    if (res.data.success && res.data.data) {
      const { token: jwtToken, user: authUser } = res.data.data;
      localStorage.setItem('jobfit_token', jwtToken);
      localStorage.setItem('jobfit_user', JSON.stringify(authUser));
      setToken(jwtToken);
      setUser(authUser);
    } else {
      throw new Error(res.data.message || 'Login failed');
    }
  };

  const register = async (fullName: string, email: string, password: string) => {
    // Purge any previous user session before registering
    localStorage.removeItem('jobfit_token');
    localStorage.removeItem('jobfit_user');
    setToken(null);
    setUser(null);

    const res = await apiClient.post<ApiResponse<AuthResponse>>('/auth/register', {
      fullName,
      email,
      password,
    });
    if (res.data.success && res.data.data) {
      const { token: jwtToken, user: authUser } = res.data.data;
      localStorage.setItem('jobfit_token', jwtToken);
      localStorage.setItem('jobfit_user', JSON.stringify(authUser));
      setToken(jwtToken);
      setUser(authUser);
    } else {
      throw new Error(res.data.message || 'Registration failed');
    }
  };

  const logout = () => {
    try {
      apiClient.post('/auth/logout').catch(() => {});
    } finally {
      localStorage.removeItem('jobfit_token');
      localStorage.removeItem('jobfit_user');
      setToken(null);
      setUser(null);
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!user && !!token,
        isLoading,
        login,
        register,
        logout,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
