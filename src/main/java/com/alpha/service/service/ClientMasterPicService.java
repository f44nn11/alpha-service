package com.alpha.service.service;

import com.alpha.service.entity.ClientMaster;
import com.alpha.service.entity.ClientMasterPic;
import com.alpha.service.repository.ClientMasterPicRepository;
import com.alpha.service.repository.ClientMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientMasterPicService {
    @Autowired
    private ClientMasterPicRepository repositoryPic;
    @Autowired
    private ClientMasterRepository repositoryMaster;
    public ClientMasterPic saveClientMasterPic(ClientMasterPic clientMasterPic) {
        return repositoryPic.save(clientMasterPic);
    }
    public ClientMaster saveClientMaster(ClientMaster clientMaster) {
        return repositoryMaster.save(clientMaster);
    }
}
