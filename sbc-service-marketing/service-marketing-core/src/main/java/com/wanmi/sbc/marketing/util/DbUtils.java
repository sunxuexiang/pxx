package com.wanmi.sbc.marketing.util;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DbUtils {
    public static <S> List<S> batchInsert(EntityManager entityManager, Iterable<S> entities) {
        Iterator<S> iterator = entities.iterator();
        List<S> list = new ArrayList<>();

        int index = 0;
        int batchSize = 1000;
        while (iterator.hasNext()) {
            S next = iterator.next();
            entityManager.persist(next);

            index++;
            if (index % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }

            list.add(next);
        }
        if (index % batchSize != 0) {
            entityManager.flush();
            entityManager.clear();
        }

        return list;
    }
}
