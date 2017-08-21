/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package automatizacionrp;

import static automatizacionrp.Automatizacionrp.log;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.StringTokenizer;
import ve.gob.mercal.app.services.Servicio;
import ve.gob.mercal.app.services.existeCampo;

/**
 *
 * @author phd2014
 */
public class obtenerParametros {
    private static Parametros param = new Parametros();
    private static String pdi = "";
    private static String cluster = "";
    private static String bd_pys = "";
    private static String tienda = "";
    private static String aux;
    private static String tipo;
    private static String id;
    private static boolean bandera = false;
    
    public static Parametros obtenerParametros(){
        log.info("Inicia proceso de obtención de parámetros");
        try{
            pdi = Servicio.queryapp("SELECT  conf.json_config , pe.id_plan_ejecucion , "
                    + "jo.job FROM public.config as conf, public.plan_ejecuciones as pe , "
                    + "public.pasos_plan_ejecucion as ppe , public.jobs as jo WHERE conf.activo=true "
                    + "and elemento='pdi' and pe.id_job=jo.id_job and pe.id_plan_ejecucion=ppe.id_plan_ejecucion "
                    + "and ppe.status_plan='en espera' and ppe.activo=true and pe.timestamp_planificacion in "
                    + "(SELECT min(pe.timestamp_planificacion) FROM public.config as conf, "
                    + "public.plan_ejecuciones as pe , public.pasos_plan_ejecucion as ppe "
                    + "WHERE conf.activo=true and elemento='pdi'and pe.id_plan_ejecucion=ppe.id_plan_ejecucion "
                    + "and ppe.status_plan='en espera' and ppe.activo=true) and pe.timestamp_planificacion::timestamp <= now()::timestamp LIMIT 1;");
           
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("Excepción obteniendo parámetros pdi 1 :");
            log.error(e);
            pdi="";
        }
        if(pdi.length()<= 2){
        log.info("No existe planificacion pendiente");
        return param;
        }
        aux = "";
        pdi = pdi.substring(1, pdi.length()-1);
        JsonParser parser = new JsonParser();
        JsonElement elementObject;
        elementObject = parser.parse(pdi);
        tipo = elementObject.getAsJsonObject().get("job").getAsString();
        id = elementObject.getAsJsonObject().get("id_plan_ejecucion").getAsString();
        
        try {
            cluster = Servicio.queryapp("SELECT  conf.json_config , pe.id_plan_ejecucion , "
                    + "jo.job FROM public.config as conf, public.plan_ejecuciones as pe , "
                    + "public.pasos_plan_ejecucion as ppe , public.jobs as jo WHERE conf.activo=true "
                    + "and elemento='cluster' and pe.id_job=jo.id_job and pe.id_plan_ejecucion=ppe.id_plan_ejecucion "
                    + "and ppe.status_plan='en espera' and ppe.activo=true and pe.timestamp_planificacion in "
                    + "(SELECT min(pe.timestamp_planificacion) FROM public.config as conf, "
                    + "public.plan_ejecuciones as pe , public.pasos_plan_ejecucion as ppe "
                    + "WHERE conf.activo=true and elemento='cluster'and pe.id_plan_ejecucion=ppe.id_plan_ejecucion "
                    + "and ppe.status_plan='en espera' and ppe.activo=true) and pe.timestamp_planificacion::timestamp <= now()::timestamp limit 1;");

            bd_pys = Servicio.queryapp("SELECT  conf.json_config , pe.id_plan_ejecucion , jo.job "
                    + "FROM public.config as conf, public.plan_ejecuciones as pe , public.pasos_plan_ejecucion as ppe, "
                    + "public.jobs as jo WHERE conf.activo=true and elemento='bd_pys' and pe.id_job=jo.id_job "
                    + "and pe.id_plan_ejecucion=ppe.id_plan_ejecucion and ppe.status_plan='en espera' and ppe.activo=true "
                    + "and pe.timestamp_planificacion in (SELECT min(pe.timestamp_planificacion) FROM public.config as conf,"
                    + "public.plan_ejecuciones as pe , public.pasos_plan_ejecucion as ppe WHERE conf.activo=true "
                    + "and elemento='bd_pys'and pe.id_plan_ejecucion=ppe.id_plan_ejecucion and ppe.status_plan='en espera' and ppe.activo=true) "
                    + "and pe.timestamp_planificacion::timestamp <= now()::timestamp limit 1;");

            tienda = Servicio.queryapp("SELECT t.tienda,t.host_bd_oracle,t.usuario_bd_oracle,t.pass_usuario_bd_oracle,t.bd_oracle\n" +
                                    "  FROM public.plan_ejecuciones as pe,public.tiendas as t\n" +
                                    "  where pe.id_plan_ejecucion="+id+" and pe.id_tienda=t.id_tienda;");

        } catch (Exception e) {
            log.error("Excepción obteniendo parámetros cluster , bd_pys y tienda :");
            log.error(e);
            tienda="";
            cluster="";
            bd_pys="";
        }
        bandera = true;

        if(pdi.length()>2){              
            if(existeCampo.existeCampo(pdi,"valor")){                                 
                aux = elementObject.getAsJsonObject().get("valor").getAsString();
                param.setTransformaciones(aux);
            }
            if(existeCampo.existeCampo(pdi,"timestamp_inicio_ejec")){                                 
                aux = elementObject.getAsJsonObject().get("timestamp_inicio_ejec").getAsString();
                param.setTimestampIni(aux);
            }
            if(existeCampo.existeCampo(pdi,"timestamp_fin_ejec")){                                 
                aux = elementObject.getAsJsonObject().get("timestamp_fin_ejec").getAsString();
                param.setTimestampFin(aux);
            }
            if(existeCampo.existeCampo(pdi,"id_plan_ejecucion")){                                 
                aux = elementObject.getAsJsonObject().get("id_plan_ejecucion").getAsString();
                param.setIdplanEjec(aux);
            }
            pdi = elementObject.getAsJsonObject().get("json_config").getAsString();
            elementObject = parser.parse(pdi); 
            if(existeCampo.existeCampo(pdi,"directorio_pdi")){                                 
                aux = elementObject.getAsJsonObject().get("directorio_pdi").getAsString();
                param.setDirPDI(aux);
            }
            if(existeCampo.existeCampo(pdi,"repositorio")){                 
                aux = elementObject.getAsJsonObject().get("repositorio").getAsString();
                param.setRepositorio(aux);
            }
            if(existeCampo.existeCampo(pdi,"usuario_repositorio")){       
                aux = elementObject.getAsJsonObject().get("usuario_repositorio").getAsString();
                param.setUsuarioRepositorio(aux);
            }
            if(existeCampo.existeCampo(pdi,"password")){        
                aux = elementObject.getAsJsonObject().get("password").getAsString();
                param.setPassusuarioRepo(aux);
            }
            if(existeCampo.existeCampo(pdi,"directorio_logs")){      
                aux = elementObject.getAsJsonObject().get("directorio_logs").getAsString();
                param.setDirLogs(aux);
            }
            if(existeCampo.existeCampo(pdi,"nivel_logs")){         
                aux = elementObject.getAsJsonObject().get("nivel_logs").getAsString();
                param.setNivelLogs(aux);
            }
            param.setNombreJob(tipo);

            if(bandera){
                if(existeCampo.existeCampo(pdi,"directorio_job_ci")){          
                    aux = elementObject.getAsJsonObject().get("directorio_job_ci").getAsString();
                    param.setDirEjecucion(aux);
                }
            }else{
                if(existeCampo.existeCampo(pdi,"directorio_job_m")){          
                    aux = elementObject.getAsJsonObject().get("directorio_job_m").getAsString();
                    param.setDirEjecucion(aux);
                }               
            }

            param.setJobModo("CARGA");
        }else{
            log.warn("No se obtuvieron los valores del PDI");
        }
        if(cluster.length()>2){
            aux = "";
            cluster = cluster.substring(1, cluster.length()-1);
            elementObject = parser.parse(cluster);                   
            cluster = elementObject.getAsJsonObject().get("json_config").getAsString();              
            if(existeCampo.existeCampo(cluster,"nodos")){
                elementObject = parser.parse(cluster);
                cluster = elementObject.getAsJsonObject().get("nodos").toString();
                cluster = cluster.substring(1, cluster.length()-1);
                StringTokenizer st = new StringTokenizer(cluster,"}");
                cluster = st.nextToken()+"}";
                elementObject = parser.parse(cluster);
                if(existeCampo.existeCampo(cluster,"host")){             
                    aux = elementObject.getAsJsonObject().get("host").getAsString();
                    param.setHostbdCassandra(aux);
                }
                if(existeCampo.existeCampo(cluster,"columnFamily")){             
                    aux = elementObject.getAsJsonObject().get("columnFamily").getAsString();
                    param.setColumnFamily(aux);
                }
                if(existeCampo.existeCampo(cluster,"keyspace")){             
                    aux = elementObject.getAsJsonObject().get("keyspace").getAsString();
                    param.setKeyspace(aux);
                }
            }
        }else{
           log.warn("No se obtuvieron los valores del cluster");
        }
        if(bd_pys.length()>2){
            aux = "";
            bd_pys = bd_pys.substring(1, bd_pys.length()-1);
            elementObject = parser.parse(bd_pys);                   
            bd_pys = elementObject.getAsJsonObject().get("json_config").getAsString();
            elementObject = parser.parse(bd_pys); 
            if(existeCampo.existeCampo(bd_pys,"host")){                                 
                aux = elementObject.getAsJsonObject().get("host").getAsString();
                param.setHostbdApp(aux);
            }
            if(existeCampo.existeCampo(bd_pys,"bd")){                                 
                aux = elementObject.getAsJsonObject().get("bd").getAsString();
                param.setBdApp(aux);
            }
            if(existeCampo.existeCampo(bd_pys,"usuario")){                                 
                aux = elementObject.getAsJsonObject().get("usuario").getAsString();
                param.setUsuariobdApp(aux);
            }
            if(existeCampo.existeCampo(bd_pys,"password")){                                 
                aux = elementObject.getAsJsonObject().get("password").getAsString();
                param.setPassusuariobdApp(aux);
            }
        }else{
           log.warn("No se obtuvieron los valores de bd_pys");
        }
        if(tienda.length()>2){
            aux = "";
            tienda = tienda.substring(1, tienda.length()-1);
            elementObject = parser.parse(tienda);                   

            if(existeCampo.existeCampo(tienda,"usuario_bd_oracle")){                                 
                aux = elementObject.getAsJsonObject().get("usuario_bd_oracle").getAsString();
                param.setUsuariobdOracle(aux);
            }
            if(existeCampo.existeCampo(tienda,"pass_usuario_bd_oracle")){                                 
                aux = elementObject.getAsJsonObject().get("pass_usuario_bd_oracle").getAsString();
                param.setPassusuariobdOracle(aux);
            }
            if(existeCampo.existeCampo(tienda,"host_bd_oracle")){                                 
                aux = elementObject.getAsJsonObject().get("host_bd_oracle").getAsString();
                param.setHostbdOracle(aux);
            }
            if(existeCampo.existeCampo(tienda,"bd_oracle")){                                 
                aux = elementObject.getAsJsonObject().get("bd_oracle").getAsString();
                param.setBdOracle(aux);
            }
        }else{
           log.warn("No se obtuvieron los valores de las tiendas");
        }
        param.setTipo(tipo);
        return param;
    }
}
