package com.application.controller;

import com.application.service.TerrainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * TerrainController
 * @Author:
 * 2023-10-05 11:03
 */
@RestController
@CrossOrigin(origins = "*")
@Api(value = "TerrainController", tags = { "TerrainController" })
public class TerrainController{
    @Autowired
    private TerrainService terrainService;
    @Value("${terrain.directory}")
    private String terrainDirectory;

    @Autowired
    private ResourceLoader resourceLoader;
    /**
     * 获取地形terrain瓦片
     * @param
     * @return
     */
    @ApiOperation("获取地形terrain瓦片")
    //@UserLoginToken
    @ResponseBody
    @GetMapping(value="/terrain/{type}/{z}/{x}/{y}.terrain",produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public Object getTerrainType(@PathVariable String type, @PathVariable Integer z, @PathVariable Integer x, @PathVariable Integer y){
        Object tileRes = terrainService.getTerrainType(type,z,x,y);
        return tileRes;
    }

    /**
     * 获取layer.json
     * @param
     * @return
     */
    @ApiOperation("获取layer.json")
    //@UserLoginToken
    @ResponseBody
    @GetMapping(value="/terrain/{type}/layer.json",produces = {MediaType.APPLICATION_JSON_VALUE})
    public Object getLayerJson(@PathVariable String type){
        Resource resource = resourceLoader.getResource("file:"+terrainDirectory+type+"-layer.json");
        String line = "";
        try{
            InputStream inputStream = resource.getInputStream();
            byte[] bytes = new byte[0];
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return new String(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }


        return line;
    }
}
