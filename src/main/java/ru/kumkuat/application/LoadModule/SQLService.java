package ru.kumkuat.application.LoadModule;

import com.mysql.cj.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Node;
import ru.kumkuat.application.GameModule.Geolocation.Geolocation;
import ru.kumkuat.application.GameModule.Models.Audio;
import ru.kumkuat.application.GameModule.Models.Picture;
import ru.kumkuat.application.GameModule.Repository.AudioRepository;
import ru.kumkuat.application.GameModule.Repository.GeolocationRepository;
import ru.kumkuat.application.GameModule.Repository.PictureRepository;


//@Repository
//@Transactional
@Component
public class SQLService {
    @Autowired
    private GeolocationRepository geolocationRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private AudioRepository audioRepository;

    public long setGeolocationIntoDB(Node triggerNode) {
        Geolocation geolocation = new Geolocation();
        var nodes = triggerNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if(nodes.item(i).getNodeName().equals("fullName")){
                geolocation.setFullName(nodes.item(i).getFirstChild().getNodeValue());
            }
            else if(nodes.item(i).getNodeName().equals("longitude")){
                geolocation.setLongitude(Double.parseDouble(nodes.item(i).getFirstChild().getNodeValue()));
            }
            else if(nodes.item(i).getNodeName().equals("latitude")){
                geolocation.setLatitude(Double.parseDouble(nodes.item(i).getFirstChild().getNodeValue()));
            }
        }
        geolocationRepository.save(geolocation);
        return geolocation.getId();
    }

    public long setPictureIntoDB(Node replyNode) {
        Picture picture = new Picture();
        var path = replyNode.getAttributes().getNamedItem("path").getNodeValue();
        picture.setPath(path);
        pictureRepository.save(picture);
        return picture.getId();
    }

    public long setAudioIntoDB(Node replyNode) {
        Audio audio = new Audio();
        var path = replyNode.getAttributes().getNamedItem("path").getNodeValue();
        audio.setPath(path);
        audioRepository.save(audio);
        return audio.getId();
    }
}

