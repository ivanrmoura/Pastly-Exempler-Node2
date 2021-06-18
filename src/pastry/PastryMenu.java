/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pastry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import rice.Continuation;
import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.p2p.past.Past;
import rice.p2p.past.PastContent;
import rice.pastry.NodeHandle;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.leafset.LeafSet;
import rice.pastry.standard.RandomNodeIdFactory;

/**
 *
 * @author laptop
 */
public class PastryMenu extends Thread {
    MyScribeClient client;
    PastryNode node;
    Environment env;
    MyApp myApp;


    PastryMenu(MyScribeClient client, PastryNode node, Environment env, MyApp myApp) {
        this.client = client;
        this.node = node;
        this.env = env;
        this.myApp = myApp;
    }

    @Override
    public void run() {
        System.out.println("MyThread running");
        try {
            System.out.println("Bem Vindo");
            Scanner sc = new Scanner(System.in);
            Scanner scm = new Scanner(System.in);
            int RES = 0;
            while (RES != 2) {
                System.out.println("------------------| Selecciona una opción |------------------");
                System.out.println("1. Subscrever");
                System.out.println("2. Enviar mensagem multicast");
                System.out.println("3. Sair");
                int in = sc.nextInt();
                if (in != 1 && in != 2 && in != 3) {
                    throw new Exception("Resposta inválida. Adios");
                } else if (in == 1) {
                    System.out.println("Você está subscrito. Agora pode publicar conteúdo.");
                    client.subscribe();
                } else if (in == 2) {
                    System.out.print("Escrever mensagem: ");
                    String msg = scm.nextLine();
                    if(msg.isEmpty()){
                        throw new Exception("Resposta inválida. Adios");
                    }
                    client.sendMulticast(msg);
                }else {
                    break;
                }
            }
        }
        catch(IOException io){
            System.out.println("Problema com o arquivo");
        }catch (Exception e) {
            System.out.println("Resposta inválida. Adios");
        } finally {
            client.unsuscribe();
            env.destroy();
            System.exit(1);

        }
    }

    private void routeMyMsg(String msg){
        NodeIdFactory nidFactory = new RandomNodeIdFactory(env);
        Id randId = nidFactory.generateNodeId();
        // send to that key
        myApp.routeMyMsg(randId, msg);
    }

    private void routeMyMsgDirect(String msg){
        LeafSet leafSet = node.getLeafSet();
        NodeHandle nh = leafSet.get(0);
        // send the message directly to the node
        myApp.routeMyMsgDirect(nh, msg);
        // wait a sec
        try {
            env.getTimeSource().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
