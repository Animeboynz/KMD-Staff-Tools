## Product File Creator

#### Run the following command and wait till the output is saved in the same directory

```
python ProductsFileGenerator.py --barcodes "Packaging and Barcodes.xlsx" --colors colors.xlsx
```

#### Example output
```
C:\Users\Animeboynz\Documents\GitHub\KMD-Staff-Tools\Product File Creator>python ProductsFileGenerator.py --barcodes "Packaging and Barcodes.xlsx" --colors colors.xlsx
Processing Barcodes & Removing Duplicates: 100%|██████████████████████████████████| 242/242 [00:00<00:00, 17286.97it/s]
Adding Products to Protobuf: 100%|████████████████████████████████████████████████| 242/242 [00:00<00:00, 22006.36it/s]
Adding Colors to Protobuf: 100%|████████████████████████████████████████████████████| 22/22 [00:00<00:00, 22022.60it/s]
✅ Protobuf file saved as products.proto.gz
```