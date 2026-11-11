package edu.crm.app.customer;

import edu.crm.domain.customer.*;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {
    private final TagGateway tags;
    private final CustomerGateway customers;
    public TagService(TagGateway tags, CustomerGateway customers) { this.tags = tags; this.customers = customers; }
    public Tag create(String name) { return tags.save(new Tag(name)); }
    public Page<Tag> list(int page, int size) { return tags.list(PageRequest.of(page, size)); }
    @Transactional
    public void delete(Long id) { Tag tag = activeTag(id); tag.delete(); tags.save(tag); }
    @Transactional
    public CustomerTag attach(Long customerId, Long tagId) {
        customers.find(customerId).orElseThrow(() -> new IllegalArgumentException("客户不存在"));
        activeTag(tagId);
        return tags.activeRelation(customerId, tagId).orElseGet(() -> tags.saveRelation(new CustomerTag(customerId, tagId)));
    }
    @Transactional
    public void detach(Long customerId, Long tagId) {
        CustomerTag relation = tags.activeRelation(customerId, tagId).orElseThrow(() -> new IllegalArgumentException("关联不存在"));
        relation.remove(); tags.saveRelation(relation);
    }
    public Page<Customer> filter(List<Long> ids, boolean all, int page, int size) {
        List<Long> distinct = ids.stream().distinct().toList();
        if (distinct.isEmpty()) throw new IllegalArgumentException("标签不能为空");
        return tags.filter(distinct, all, PageRequest.of(page, size));
    }
    private Tag activeTag(Long id) {
        Tag tag = tags.find(id).orElseThrow(() -> new IllegalArgumentException("标签不存在"));
        if (tag.isDeleted()) throw new IllegalArgumentException("标签已删除");
        return tag;
    }
}
