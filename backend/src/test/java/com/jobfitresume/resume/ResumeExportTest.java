package com.jobfitresume.resume;

import com.jobfitresume.resume.dto.ResumeContentModel;
import com.jobfitresume.resume.export.DocxResumeExporter;
import com.jobfitresume.resume.export.PdfResumeExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResumeExportTest {

    private PdfResumeExporter pdfExporter;
    private DocxResumeExporter docxExporter;
    private ResumeContentModel testModel;

    @BeforeEach
    void setUp() {
        pdfExporter = new PdfResumeExporter();
        docxExporter = new DocxResumeExporter();

        testModel = new ResumeContentModel();
        testModel.getContact().fullName = "Alex Mercer";
        testModel.getContact().headline = "Senior Full Stack Engineer";
        testModel.getContact().email = "alex@example.com";
        testModel.getContact().phone = "+1 555-0199";
        testModel.getContact().location = "Seattle, WA";
        testModel.getContact().githubUrl = "https://github.com/alex";
        testModel.getContact().linkedinUrl = "https://linkedin.com/in/alex";

        testModel.setProfessionalSummary("Senior Full Stack Developer with 7+ years of experience designing robust microservices and interactive user interfaces.");
        testModel.setPrimarySkills(List.of("Java", "Spring Boot", "React", "TypeScript", "PostgreSQL", "Docker"));
        testModel.setSecondarySkills(List.of("Kubernetes", "AWS", "Redis", "CI/CD"));

        ResumeContentModel.ExperienceItem exp = new ResumeContentModel.ExperienceItem();
        exp.jobTitle = "Lead Software Engineer";
        exp.employer = "CloudScale Tech";
        exp.location = "San Francisco, CA";
        exp.startDate = "2021";
        exp.endDate = "Present";
        exp.currentRole = true;
        exp.description = "Led a cross-functional engineering team building distributed enterprise cloud services.";
        exp.bulletPoints = List.of(
                "Engineered high-throughput Java 21 microservices handling 40M+ daily events.",
                "Reduced P99 API latencies by 35% through Redis caching and query indexing."
        );
        testModel.setExperiences(List.of(exp));

        ResumeContentModel.ProjectItem proj = new ResumeContentModel.ProjectItem();
        proj.name = "JobFit Resume Engine";
        proj.technicalStack = "Java 21, Spring Boot 3, React 19, OpenHTMLtoPDF";
        proj.description = "Full stack career profile management and ATS resume generator.";
        proj.outcomes = "Generated over 10,000 tailored resumes with 99.8% export success.";
        testModel.setProjects(List.of(proj));

        ResumeContentModel.EducationItem edu = new ResumeContentModel.EducationItem();
        edu.degree = "Bachelor of Science";
        edu.fieldOfStudy = "Computer Science";
        edu.institution = "University of Washington";
        edu.startDate = "2014";
        edu.endDate = "2018";
        testModel.setEducations(List.of(edu));
    }

    @Test
    void testExportPdfClassic() {
        byte[] pdf = pdfExporter.exportToPdf(testModel, "classic");
        assertNotNull(pdf);
        assertTrue(pdf.length > 1000, "PDF byte array should contain valid PDF payload");
        // Check PDF magic header %PDF-
        assertEquals('%', (char) pdf[0]);
        assertEquals('P', (char) pdf[1]);
        assertEquals('D', (char) pdf[2]);
        assertEquals('F', (char) pdf[3]);
    }

    @Test
    void testExportPdfModernAndTechnical() {
        byte[] modern = pdfExporter.exportToPdf(testModel, "modern");
        assertNotNull(modern);
        assertTrue(modern.length > 1000);

        byte[] tech = pdfExporter.exportToPdf(testModel, "technical");
        assertNotNull(tech);
        assertTrue(tech.length > 1000);
    }

    @Test
    void testExportDocx() {
        byte[] docx = docxExporter.exportToDocx(testModel);
        assertNotNull(docx);
        assertTrue(docx.length > 1000, "DOCX byte array should contain valid OOXML archive");
        // Check zip magic header PK
        assertEquals('P', (char) docx[0]);
        assertEquals('K', (char) docx[1]);
    }
}
