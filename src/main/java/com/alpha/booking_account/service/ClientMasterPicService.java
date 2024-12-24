package com.alpha.booking_account.service;

import com.alpha.booking_account.entity.ClientMaster;
import com.alpha.booking_account.entity.ClientMasterPic;
import com.alpha.booking_account.repository.ClientMasterPicRepository;
import com.alpha.booking_account.repository.ClientMasterRepository;
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
