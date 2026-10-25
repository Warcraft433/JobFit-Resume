package com.jobfitresume.resume.export;

import com.jobfitresume.resume.dto.ResumeContentModel;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class PdfResumeExporter {

    public byte[] exportToPdf(ResumeContentModel model, String templateKey) {
        String html = generateHtml(model, templateKey != null ? templateKey.toLowerCase() : "classic");
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, "");
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to render PDF resume: " + e.getMessage(), e);
        }
    }

    private String generateHtml(ResumeContentModel model, String template) {
        ResumeContentModel.ContactInfo c = model.getContact();

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"/><style>");
        sb.append("@page { size: A4; margin: 18mm 16mm 18mm 16mm; }");
        sb.append("body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; color: #1e293b; line-height: 1.45; font-size: 10.5pt; margin: 0; padding: 0; }");
        sb.append("h1, h2, h3, h4, p { margin: 0; padding: 0; }");
        sb.append(".header { text-align: center; margin-bottom: 14pt; }");
        sb.append(".name { font-size: 22pt; font-weight: bold; color: #0f172a; margin-bottom: 4pt; }");
        sb.append(".headline { font-size: 12pt; color: #475569; font-weight: 500; margin-bottom: 6pt; }");
        sb.append(".contact { font-size: 9.5pt; color: #64748b; }");
        sb.append(".contact span { margin: 0 4pt; }");
        sb.append(".section { margin-bottom: 12pt; }");
        sb.append(".section-title { font-size: 11.5pt; font-weight: bold; text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 1.5pt solid #cbd5e1; padding-bottom: 2pt; margin-bottom: 6pt; color: #0f172a; }");
        sb.append(".summary-text { font-size: 10pt; color: #334155; text-align: justify; margin-bottom: 8pt; }");
        sb.append(".item { margin-bottom: 8pt; }");
        sb.append(".item-table { width: 100%; border-collapse: collapse; margin-bottom: 2pt; }");
        sb.append(".item-table td { padding: 0; vertical-align: top; }");
        sb.append(".item-title { font-weight: bold; color: #0f172a; font-size: 10.5pt; }");
        sb.append(".item-company { font-weight: 600; color: #2563eb; font-size: 10.5pt; }");
        sb.append(".item-date { font-size: 9.5pt; color: #64748b; font-weight: normal; text-align: right; white-space: nowrap; }");
        sb.append(".item-sub { font-size: 9.5pt; color: #475569; margin-bottom: 3pt; }");
        sb.append(".bullet-list { margin: 3pt 0 0 16pt; padding: 0; }");
        sb.append(".bullet-list li { margin-bottom: 2.5pt; font-size: 9.5pt; color: #334155; }");
        sb.append(".skills-grid { margin-top: 4pt; }");
        sb.append(".skill-category { margin-bottom: 4pt; font-size: 9.5pt; }");
        sb.append(".skill-label { font-weight: bold; color: #1e293b; }");
        sb.append(".skill-tags { color: #334155; }");

        // Custom template styles
        if ("modern".equals(template)) {
            sb.append(".header { text-align: left; border-left: 4pt solid #2563eb; padding-left: 10pt; margin-bottom: 14pt; }");
            sb.append(".section-title { border-bottom: 2pt solid #2563eb; color: #1d4ed8; }");
        } else if ("technical".equals(template)) {
            sb.append("body { font-size: 10pt; }");
            sb.append(".section-title { font-size: 10.5pt; border-bottom: 1pt solid #0f172a; color: #0284c7; }");
            sb.append(".tech-tag { font-family: monospace; font-size: 9pt; background-color: #f1f5f9; padding: 1pt 3pt; border-radius: 2pt; margin-right: 2pt; }");
        }

        sb.append("</style></head><body>");

        // Header
        sb.append("<div class=\"header\">");
        sb.append("<div class=\"name\">").append(escape(c.fullName)).append("</div>");
        if (c.headline != null && !c.headline.isEmpty()) {
            sb.append("<div class=\"headline\">").append(escape(c.headline)).append("</div>");
        }
        sb.append("<div class=\"contact\">");
        boolean hasPrev = false;
        if (c.email != null && !c.email.isEmpty()) {
            sb.append(escape(c.email));
            hasPrev = true;
        }
        if (c.phone != null && !c.phone.isEmpty()) {
            if (hasPrev) sb.append("<span>•</span>");
            sb.append(escape(c.phone));
            hasPrev = true;
        }
        if (c.location != null && !c.location.isEmpty()) {
            if (hasPrev) sb.append("<span>•</span>");
            sb.append(escape(c.location));
            hasPrev = true;
        }
        if (c.linkedinUrl != null && !c.linkedinUrl.isEmpty()) {
            if (hasPrev) sb.append("<span>•</span>");
            sb.append(escape(c.linkedinUrl));
            hasPrev = true;
        }
        if (c.githubUrl != null && !c.githubUrl.isEmpty()) {
            if (hasPrev) sb.append("<span>•</span>");
            sb.append(escape(c.githubUrl));
            hasPrev = true;
        }
        if (c.portfolioUrl != null && !c.portfolioUrl.isEmpty()) {
            if (hasPrev) sb.append("<span>•</span>");
            sb.append(escape(c.portfolioUrl));
            hasPrev = true;
        }
        if (c.customLinks != null && !c.customLinks.isEmpty()) {
            try {
                if (c.customLinks.startsWith("[")) {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    java.util.List<java.util.Map<String, String>> links = mapper.readValue(c.customLinks, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.util.Map<String, String>>>(){});
                    for (java.util.Map<String, String> link : links) {
                        String url = link.getOrDefault("url", "");
                        String label = link.getOrDefault("label", "");
                        if (!url.isEmpty()) {
                            if (hasPrev) sb.append("<span>•</span>");
                            sb.append(!label.isEmpty() ? escape(label) + ": " : "").append(escape(url));
                            hasPrev = true;
                        }
                    }
                } else {
                    if (hasPrev) sb.append("<span>•</span>");
                    sb.append(escape(c.customLinks));
                }
            } catch (Exception ignored) {
                if (hasPrev) sb.append("<span>•</span>");
                sb.append(escape(c.customLinks));
            }
        }
        sb.append("</div></div>");

        // Professional Summary
        if (model.getProfessionalSummary() != null && !model.getProfessionalSummary().isEmpty()) {
            sb.append("<div class=\"section\">");
            sb.append("<div class=\"section-title\">Professional Summary</div>");
            sb.append("<div class=\"summary-text\">").append(escape(model.getProfessionalSummary())).append("</div>");
            sb.append("</div>");
        }

        // Skills
        if (!model.getPrimarySkills().isEmpty() || !model.getSecondarySkills().isEmpty()) {
            sb.append("<div class=\"section\">");
            sb.append("<div class=\"section-title\">Technical Skills</div>");
            sb.append("<div class=\"skills-grid\">");
            if (!model.getPrimarySkills().isEmpty()) {
                sb.append("<div class=\"skill-category\"><span class=\"skill-label\">Core Technologies: </span><span class=\"skill-tags\">")
                        .append(escape(String.join(", ", model.getPrimarySkills()))).append("</span></div>");
            }
            if (!model.getSecondarySkills().isEmpty()) {
                sb.append("<div class=\"skill-category\"><span class=\"skill-label\">Additional Tools &amp; Skills: </span><span class=\"skill-tags\">")
                        .append(escape(String.join(", ", model.getSecondarySkills()))).append("</span></div>");
            }
            sb.append("</div></div>");
        }

        // Experience
        if (!model.getExperiences().isEmpty()) {
            sb.append("<div class=\"section\">");
            sb.append("<div class=\"section-title\">Work Experience</div>");
            for (ResumeContentModel.ExperienceItem exp : model.getExperiences()) {
                sb.append("<div class=\"item\">");
                sb.append("<table class=\"item-table\"><tr>");
                sb.append("<td><span class=\"item-title\">").append(escape(exp.jobTitle)).append("</span> — <span class=\"item-company\">").append(escape(exp.employer)).append("</span></td>");
                sb.append("<td class=\"item-date\">").append(escape(exp.startDate)).append(" – ").append(exp.currentRole ? "Present" : escape(exp.endDate)).append("</td>");
                sb.append("</tr></table>");
                if (exp.location != null && !exp.location.isEmpty()) {
                    sb.append("<div class=\"item-sub\">").append(escape(exp.location)).append("</div>");
                }
                if (exp.description != null && !exp.description.isEmpty()) {
                    sb.append("<p style=\"font-size: 9.5pt; color: #334155; margin-bottom: 2pt;\">").append(escape(exp.description)).append("</p>");
                }
                if (!exp.bulletPoints.isEmpty()) {
                    sb.append("<ul class=\"bullet-list\">");
                    for (String bp : exp.bulletPoints) {
                        sb.append("<li>").append(escape(bp)).append("</li>");
                    }
                    sb.append("</ul>");
                }
                sb.append("</div>");
            }
            sb.append("</div>");
        }

        // Projects
        if (!model.getProjects().isEmpty()) {
            sb.append("<div class=\"section\">");
            sb.append("<div class=\"section-title\">Key Projects</div>");
            for (ResumeContentModel.ProjectItem proj : model.getProjects()) {
                sb.append("<div class=\"item\">");
                sb.append("<table class=\"item-table\"><tr>");
                sb.append("<td><span class=\"item-title\">").append(escape(proj.name)).append("</span></td>");
                if (proj.technicalStack != null && !proj.technicalStack.isEmpty()) {
                    sb.append("<td class=\"item-date\" style=\"color: #0284c7; font-weight: 500;\">").append(escape(proj.technicalStack)).append("</td>");
                }
                sb.append("</tr></table>");
                if (proj.description != null && !proj.description.isEmpty()) {
                    sb.append("<p style=\"font-size: 9.5pt; color: #334155; margin-top: 2pt;\">").append(escape(proj.description)).append("</p>");
                }
                if (proj.outcomes != null && !proj.outcomes.isEmpty()) {
                    sb.append("<p style=\"font-size: 9pt; color: #166534; font-weight: 500; margin-top: 1pt;\">Impact: ").append(escape(proj.outcomes)).append("</p>");
                }
                sb.append("</div>");
            }
            sb.append("</div>");
        }

        // Education
        if (!model.getEducations().isEmpty()) {
            sb.append("<div class=\"section\">");
            sb.append("<div class=\"section-title\">Education</div>");
            for (ResumeContentModel.EducationItem edu : model.getEducations()) {
                sb.append("<div class=\"item\">");
                sb.append("<table class=\"item-table\"><tr>");
                sb.append("<td><span class=\"item-title\">").append(escape(edu.degree)).append(edu.fieldOfStudy != null && !edu.fieldOfStudy.isEmpty() ? " in " + escape(edu.fieldOfStudy) : "").append("</span></td>");
                sb.append("<td class=\"item-date\">").append(escape(edu.startDate)).append(" – ").append(edu.currentlyStudying ? "Present" : escape(edu.endDate)).append("</td>");
                sb.append("</tr></table>");
                sb.append("<div class=\"item-sub\">").append(escape(edu.institution));
                if (edu.grade != null && !edu.grade.isEmpty()) {
                    sb.append(" (").append(escape(edu.grade)).append(")");
                }
                sb.append("</div>");
                sb.append("</div>");
            }
            sb.append("</div>");
        }

        // Certifications
        if (!model.getCertifications().isEmpty()) {
            sb.append("<div class=\"section\">");
            sb.append("<div class=\"section-title\">Certifications</div>");
            for (ResumeContentModel.CertificationItem cert : model.getCertifications()) {
                sb.append("<div style=\"font-size: 9.5pt; margin-bottom: 2pt;\">");
                sb.append("<strong>").append(escape(cert.name)).append("</strong> — ").append(escape(cert.issuer));
                if (cert.issueDate != null && !cert.issueDate.isEmpty()) {
                    sb.append(" (").append(escape(cert.issueDate)).append(")");
                }
                sb.append("</div>");
            }
            sb.append("</div>");
        }

        sb.append("</body></html>");
        return sb.toString();
    }

    private String escape(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
