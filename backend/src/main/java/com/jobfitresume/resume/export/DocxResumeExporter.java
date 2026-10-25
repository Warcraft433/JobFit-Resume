package com.jobfitresume.resume.export;

import com.jobfitresume.resume.dto.ResumeContentModel;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class DocxResumeExporter {

    public byte[] exportToDocx(ResumeContentModel model) {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            ResumeContentModel.ContactInfo c = model.getContact();

            // Name
            XWPFParagraph namePara = document.createParagraph();
            namePara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun nameRun = namePara.createRun();
            nameRun.setText(c.fullName != null && !c.fullName.isEmpty() ? c.fullName : "Candidate");
            nameRun.setBold(true);
            nameRun.setFontSize(20);
            nameRun.setFontFamily("Arial");
            nameRun.setColor("0F172A");

            // Headline
            if (c.headline != null && !c.headline.isEmpty()) {
                XWPFParagraph headlinePara = document.createParagraph();
                headlinePara.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun headlineRun = headlinePara.createRun();
                headlineRun.setText(c.headline);
                headlineRun.setFontSize(12);
                headlineRun.setFontFamily("Arial");
                headlineRun.setColor("475569");
            }

            // Contact
            XWPFParagraph contactPara = document.createParagraph();
            contactPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun contactRun = contactPara.createRun();
            StringBuilder contactText = new StringBuilder();
            if (c.email != null && !c.email.isEmpty()) contactText.append(c.email);
            if (c.phone != null && !c.phone.isEmpty()) {
                if (contactText.length() > 0) contactText.append(" | ");
                contactText.append(c.phone);
            }
            if (c.location != null && !c.location.isEmpty()) {
                if (contactText.length() > 0) contactText.append(" | ");
                contactText.append(c.location);
            }
            if (c.linkedinUrl != null && !c.linkedinUrl.isEmpty()) {
                if (contactText.length() > 0) contactText.append(" | ");
                contactText.append(c.linkedinUrl);
            }
            if (c.githubUrl != null && !c.githubUrl.isEmpty()) {
                if (contactText.length() > 0) contactText.append(" | ");
                contactText.append(c.githubUrl);
            }
            if (c.portfolioUrl != null && !c.portfolioUrl.isEmpty()) {
                if (contactText.length() > 0) contactText.append(" | ");
                contactText.append(c.portfolioUrl);
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
                                if (contactText.length() > 0) contactText.append(" | ");
                                contactText.append(!label.isEmpty() ? label + ": " : "").append(url);
                            }
                        }
                    } else {
                        if (contactText.length() > 0) contactText.append(" | ");
                        contactText.append(c.customLinks);
                    }
                } catch (Exception ignored) {
                    if (contactText.length() > 0) contactText.append(" | ");
                    contactText.append(c.customLinks);
                }
            }
            contactRun.setText(contactText.toString());
            contactRun.setFontSize(9);
            contactRun.setFontFamily("Arial");
            contactRun.setColor("64748B");

            // Professional Summary
            if (model.getProfessionalSummary() != null && !model.getProfessionalSummary().isEmpty()) {
                addSectionHeading(document, "PROFESSIONAL SUMMARY");
                XWPFParagraph summaryPara = document.createParagraph();
                XWPFRun summaryRun = summaryPara.createRun();
                summaryRun.setText(model.getProfessionalSummary());
                summaryRun.setFontSize(10);
                summaryRun.setFontFamily("Arial");
                summaryRun.setColor("334155");
            }

            // Skills
            if (!model.getPrimarySkills().isEmpty() || !model.getSecondarySkills().isEmpty()) {
                addSectionHeading(document, "TECHNICAL SKILLS");
                if (!model.getPrimarySkills().isEmpty()) {
                    XWPFParagraph skillPara1 = document.createParagraph();
                    XWPFRun l1 = skillPara1.createRun();
                    l1.setBold(true);
                    l1.setFontSize(10);
                    l1.setFontFamily("Arial");
                    l1.setText("Core Technologies: ");
                    XWPFRun v1 = skillPara1.createRun();
                    v1.setFontSize(10);
                    v1.setFontFamily("Arial");
                    v1.setText(String.join(", ", model.getPrimarySkills()));
                }
                if (!model.getSecondarySkills().isEmpty()) {
                    XWPFParagraph skillPara2 = document.createParagraph();
                    XWPFRun l2 = skillPara2.createRun();
                    l2.setBold(true);
                    l2.setFontSize(10);
                    l2.setFontFamily("Arial");
                    l2.setText("Tools & Frameworks: ");
                    XWPFRun v2 = skillPara2.createRun();
                    v2.setFontSize(10);
                    v2.setFontFamily("Arial");
                    v2.setText(String.join(", ", model.getSecondarySkills()));
                }
            }

            // Experience
            if (!model.getExperiences().isEmpty()) {
                addSectionHeading(document, "WORK EXPERIENCE");
                for (ResumeContentModel.ExperienceItem exp : model.getExperiences()) {
                    XWPFParagraph expHeader = document.createParagraph();
                    XWPFRun rTitle = expHeader.createRun();
                    rTitle.setBold(true);
                    rTitle.setFontSize(10);
                    rTitle.setFontFamily("Arial");
                    rTitle.setText(exp.jobTitle + " — ");

                    XWPFRun rCompany = expHeader.createRun();
                    rCompany.setBold(true);
                    rCompany.setColor("2563EB");
                    rCompany.setFontSize(10);
                    rCompany.setFontFamily("Arial");
                    rCompany.setText(exp.employer);

                    XWPFRun rDates = expHeader.createRun();
                    rDates.setFontSize(9);
                    rDates.setFontFamily("Arial");
                    rDates.setColor("64748B");
                    rDates.setText("  (" + exp.startDate + " – " + (exp.currentRole ? "Present" : exp.endDate) + ")");

                    if (exp.description != null && !exp.description.isEmpty()) {
                        XWPFParagraph descPara = document.createParagraph();
                        XWPFRun descRun = descPara.createRun();
                        descRun.setFontSize(9);
                        descRun.setFontFamily("Arial");
                        descRun.setText(exp.description);
                    }

                    for (String bullet : exp.bulletPoints) {
                        XWPFParagraph bulletPara = document.createParagraph();
                        bulletPara.setIndentationLeft(360);
                        XWPFRun bulletRun = bulletPara.createRun();
                        bulletRun.setFontSize(9);
                        bulletRun.setFontFamily("Arial");
                        bulletRun.setText("• " + bullet);
                    }
                }
            }

            // Projects
            if (!model.getProjects().isEmpty()) {
                addSectionHeading(document, "KEY PROJECTS");
                for (ResumeContentModel.ProjectItem proj : model.getProjects()) {
                    XWPFParagraph projPara = document.createParagraph();
                    XWPFRun pName = projPara.createRun();
                    pName.setBold(true);
                    pName.setFontSize(10);
                    pName.setFontFamily("Arial");
                    pName.setText(proj.name);

                    if (proj.technicalStack != null && !proj.technicalStack.isEmpty()) {
                        XWPFRun pStack = projPara.createRun();
                        pStack.setFontSize(9);
                        pStack.setColor("0284C7");
                        pStack.setFontFamily("Arial");
                        pStack.setText(" | " + proj.technicalStack);
                    }

                    if (proj.description != null && !proj.description.isEmpty()) {
                        XWPFParagraph pDesc = document.createParagraph();
                        pDesc.setIndentationLeft(200);
                        XWPFRun rDesc = pDesc.createRun();
                        rDesc.setFontSize(9);
                        rDesc.setFontFamily("Arial");
                        rDesc.setText(proj.description);
                    }
                    if (proj.outcomes != null && !proj.outcomes.isEmpty()) {
                        XWPFParagraph pOut = document.createParagraph();
                        pOut.setIndentationLeft(200);
                        XWPFRun rOut = pOut.createRun();
                        rOut.setFontSize(9);
                        rOut.setColor("166534");
                        rOut.setFontFamily("Arial");
                        rOut.setText("Impact: " + proj.outcomes);
                    }
                }
            }

            // Education
            if (!model.getEducations().isEmpty()) {
                addSectionHeading(document, "EDUCATION");
                for (ResumeContentModel.EducationItem edu : model.getEducations()) {
                    XWPFParagraph eduPara = document.createParagraph();
                    XWPFRun eTitle = eduPara.createRun();
                    eTitle.setBold(true);
                    eTitle.setFontSize(10);
                    eTitle.setFontFamily("Arial");
                    eTitle.setText(edu.degree + (edu.fieldOfStudy != null && !edu.fieldOfStudy.isEmpty() ? " in " + edu.fieldOfStudy : ""));

                    XWPFRun eInst = eduPara.createRun();
                    eInst.setFontSize(9);
                    eInst.setFontFamily("Arial");
                    String gradeStr = (edu.grade != null && !edu.grade.isEmpty()) ? " [CGPA/Grade: " + edu.grade + "]" : "";
                    eInst.setText(" — " + edu.institution + gradeStr + " (" + edu.startDate + " – " + (edu.currentlyStudying ? "Present" : edu.endDate) + ")");
                }
            }

            // Certifications
            if (!model.getCertifications().isEmpty()) {
                addSectionHeading(document, "CERTIFICATIONS");
                for (ResumeContentModel.CertificationItem cert : model.getCertifications()) {
                    XWPFParagraph certPara = document.createParagraph();
                    XWPFRun cName = certPara.createRun();
                    cName.setBold(true);
                    cName.setFontSize(9);
                    cName.setFontFamily("Arial");
                    cName.setText(cert.name);

                    XWPFRun cIss = certPara.createRun();
                    cIss.setFontSize(9);
                    cIss.setFontFamily("Arial");
                    cIss.setText(" — " + cert.issuer + (cert.issueDate != null && !cert.issueDate.isEmpty() ? " (" + cert.issueDate + ")" : ""));
                }
            }

            document.write(os);
            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate DOCX document: " + e.getMessage(), e);
        }
    }

    private void addSectionHeading(XWPFDocument document, String title) {
        XWPFParagraph sectionPara = document.createParagraph();
        sectionPara.setSpacingBefore(180);
        sectionPara.setSpacingAfter(60);
        XWPFRun run = sectionPara.createRun();
        run.setText(title);
        run.setBold(true);
        run.setFontSize(11);
        run.setFontFamily("Arial");
        run.setColor("0F172A");
    }
}
