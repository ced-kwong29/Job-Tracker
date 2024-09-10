//package com.cedrickwong.backend.repository;
//
//import com.cedrickwong.backend.model.Application.Status;
//import com.cedrickwong.backend.model.Job;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.Query;
//import jakarta.transaction.Transactional;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//
//@Repository
//public class ApplicationRepositoryCustomImpl implements ApplicationRepositoryCustom {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    public void update(Long id, Job job, LocalDate date, Status status) {
//        StringBuilder stringBuilder = new StringBuilder("UPDATE Application a SET ");
//
//        boolean mustUpdate = false;
//
//        if (job != null) {
//            mustUpdate = true;
//            stringBuilder.append("a.job = :job");
//        }
//        if (date != null) {
//            if (mustUpdate) {
//                stringBuilder.append(", ");
//            }
//            stringBuilder.append("a.date = :date");
//            mustUpdate = true;
//        }
//        if (status != null) {
//            if (mustUpdate) {
//                stringBuilder.append(", ");
//            }
//            stringBuilder.append("a.status = :status");
//        }
//        stringBuilder.append(" WHERE a.id = :id");
//
//        Query query = entityManager.createQuery(stringBuilder.toString());
//        query.setParameter("id", id);
//
//        if (job != null) {
//            query.setParameter("job", job);
//        }
//        if (date != null) {
//            query.setParameter("date", date);
//        }
//        if (status != null) {
//            query.setParameter("status", status);
//        }
//        query.executeUpdate();
//    }
//}
