package com.example.demo.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para integración con Google Drive.
 * Permite subir archivos y obtener links compartidos.
 */
@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "Seguimiento Reportes";
    
    @Value("${google.drive.credentials.path:#{null}}")
    private String credentialsPath;
    
    @Value("${google.drive.folder.id:#{null}}")
    private String folderId;
    
    @Value("${google.drive.enabled:false}")
    private boolean driveEnabled;
    
    private Drive driveService;
    
    @PostConstruct
    public void init() {
        if (driveEnabled && credentialsPath != null) {
            try {
                driveService = createDriveService();
                System.out.println("✓ Google Drive Service inicializado correctamente");
            } catch (Exception e) {
                System.err.println("⚠ Error inicializando Google Drive: " + e.getMessage());
                driveEnabled = false;
            }
        } else {
            System.out.println("ℹ Google Drive deshabilitado. Los archivos se guardarán localmente.");
        }
    }
    
    private Drive createDriveService() throws GeneralSecurityException, IOException {
        InputStream credentialsStream = getClass().getResourceAsStream(credentialsPath);
        if (credentialsStream == null) {
            throw new IOException("No se encontró el archivo de credenciales: " + credentialsPath);
        }
        
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));
        
        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    /**
     * Sube un archivo a Google Drive y retorna información del archivo.
     */
    public Map<String, String> uploadFile(MultipartFile multipartFile, String reporteId, String periodo) throws IOException {
        Map<String, String> result = new HashMap<>();
        
        if (!driveEnabled || driveService == null) {
            // Modo sin Drive: simular subida
            result.put("fileId", "local_" + System.currentTimeMillis());
            result.put("fileName", multipartFile.getOriginalFilename());
            result.put("webViewLink", "#"); // Sin link real
            result.put("webContentLink", "#");
            result.put("mode", "local");
            return result;
        }
        
        try {
            // Crear metadata del archivo
            String fileName = String.format("%s_%s_%s", 
                    reporteId, 
                    periodo, 
                    multipartFile.getOriginalFilename());
            
            File fileMetadata = new File();
            fileMetadata.setName(fileName);
            
            // Si hay carpeta configurada, subir ahí
            if (folderId != null && !folderId.isEmpty()) {
                fileMetadata.setParents(Collections.singletonList(folderId));
            }
            
            // Subir archivo
            InputStreamContent mediaContent = new InputStreamContent(
                    multipartFile.getContentType(),
                    new ByteArrayInputStream(multipartFile.getBytes()));
            
            File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, name, webViewLink, webContentLink")
                    .execute();
            
            // Hacer el archivo público o compartido con la organización
            Permission permission = new Permission();
            permission.setType("anyone");
            permission.setRole("reader");
            driveService.permissions().create(uploadedFile.getId(), permission).execute();
            
            result.put("fileId", uploadedFile.getId());
            result.put("fileName", uploadedFile.getName());
            result.put("webViewLink", uploadedFile.getWebViewLink());
            result.put("webContentLink", uploadedFile.getWebContentLink());
            result.put("mode", "drive");
            
            return result;
            
        } catch (Exception e) {
            throw new IOException("Error subiendo archivo a Google Drive: " + e.getMessage(), e);
        }
    }
    
    /**
     * Elimina un archivo de Google Drive.
     */
    public void deleteFile(String fileId) throws IOException {
        if (!driveEnabled || driveService == null || fileId == null || fileId.startsWith("local_")) {
            return;
        }
        
        try {
            driveService.files().delete(fileId).execute();
        } catch (Exception e) {
            System.err.println("Error eliminando archivo de Drive: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si Drive está habilitado.
     */
    public boolean isDriveEnabled() {
        return driveEnabled && driveService != null;
    }
}
