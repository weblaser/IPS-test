package com.ctl.security.ips.service;

import com.ctl.security.dsm.DsmPolicyClient;
import com.ctl.security.dsm.domain.CtlSecurityProfile;
import manager.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by kevin.wilde on 1/19/2015.
 */
public class PolicyService {

    @Autowired
    private DsmPolicyClient dsmPolicyClient;

    public CtlSecurityProfile createPolicy(CtlSecurityProfile ctlSecurityProfileToBeCreated) {
        CtlSecurityProfile newlyCreatedCtlSecurityProfile = null;
        try {
            newlyCreatedCtlSecurityProfile = dsmPolicyClient.createCtlSecurityProfile(ctlSecurityProfileToBeCreated);
        } catch (ManagerLockoutException_Exception e) {
            e.printStackTrace();
        } catch (ManagerAuthenticationException_Exception e) {
            e.printStackTrace();
        } catch (ManagerException_Exception e) {
            e.printStackTrace();
        } catch (ManagerIntegrityConstraintException_Exception e) {
            e.printStackTrace();
        } catch (ManagerSecurityException_Exception e) {
            e.printStackTrace();
        } catch (ManagerValidationException_Exception e) {
            e.printStackTrace();
        } catch (ManagerCommunicationException_Exception e) {
            e.printStackTrace();
        } catch (ManagerMaxSessionsException_Exception e) {
            e.printStackTrace();
        } catch (ManagerAuthorizationException_Exception e) {
            e.printStackTrace();
        } catch (ManagerTimeoutException_Exception e) {
            e.printStackTrace();
        }
        return newlyCreatedCtlSecurityProfile;
    }
}
