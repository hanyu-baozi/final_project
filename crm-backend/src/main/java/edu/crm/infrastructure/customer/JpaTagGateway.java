package edu.crm.infrastructure.customer;

import edu.crm.domain.customer.*;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

@Component
public class JpaTagGateway implements TagGateway {
    private final TagRepository tags;
    private final CustomerTagRepository relations;
    private final EntityManager em;
    public JpaTagGateway(TagRepository tags, CustomerTagRepository relations, EntityManager em) {
        this.tags = tags; this.relations = relations; this.em = em;
    }
    public Tag save(Tag tag) { return tags.save(tag); }
    public Optional<Tag> find(Long id) { return tags.findById(id); }
    public Page<Tag> list(Pageable page) { return tags.findByDeletedFalse(page); }
    public CustomerTag saveRelation(CustomerTag relation) { return relations.save(relation); }
    public Optional<CustomerTag> activeRelation(Long customerId, Long tagId) {
        return relations.findFirstByCustomerIdAndTagIdAndActiveTrue(customerId, tagId);
    }
    public Page<Customer> filter(List<Long> ids, boolean all, Pageable page) {
        String filter = " from Customer c where c.id in (select r.customerId from CustomerTag r, Tag t "
                + "where r.tagId=t.id and r.active=true and t.deleted=false and r.tagId in :ids "
                + "group by r.customerId having count(distinct r.tagId) >= :required)";
        long required = all ? ids.size() : 1L;
        long count = em.createQuery("select count(c)" + filter, Long.class)
                .setParameter("ids", ids).setParameter("required", required).getSingleResult();
        List<Customer> content = em.createQuery("select c" + filter + " order by c.id", Customer.class)
                .setParameter("ids", ids).setParameter("required", required)
                .setFirstResult(Math.toIntExact(page.getOffset())).setMaxResults(page.getPageSize()).getResultList();
        return new PageImpl<>(content, page, count);
    }
}
