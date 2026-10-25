-- V2 Migration: Add custom_links to user_profiles and show_grade_on_resume to education
ALTER TABLE user_profiles ADD COLUMN custom_links TEXT;
ALTER TABLE education ADD COLUMN show_grade_on_resume BOOLEAN NOT NULL DEFAULT TRUE;
