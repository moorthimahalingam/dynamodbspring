package org.moorthi.springboot.dynamodbspring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.moorthi.springboot.dynamodbspring.repository.ApplicationRepository;
import org.moorthi.springboot.dynamodbspring.domain.ApplicationItem;
import java.util.List;


/**
 * Created by MRomeh
 */
@Service
public class ApplicationService {
    private static final Logger log = LoggerFactory.getLogger(ApplicationService.class);
    @Autowired
    private ApplicationRepository applicationRepository;

    @Transactional
    public void createApplicationItem(ApplicationItem applicationItem) {
        applicationRepository.save(applicationItem);
    }

    public List<ApplicationItem> getApplicationItems() {
      return applicationRepository.findAll();
    }


}
