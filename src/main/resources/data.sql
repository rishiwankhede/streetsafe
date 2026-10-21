-- StreetSafe seed data.
-- Explicit primary keys + INSERT  make this script safe to run on every start-up
-- (spring.sql.init.mode=always) without creating duplicates.

INSERT INTO vendor (id, stall_name, owner_name, location, fssai_number, fssai_verified) VALUES
                                                                                            (1, 'Raju Sandwich Corner', 'Rajesh K. Mehta', 'Opp. ICICI Bank, Shimpoli Road, Borivali West', '11521015000412', TRUE),
                                                                                            (2, 'Shree Ram Krupa Snacks (Snack Shack)', 'Mahesh H. Patel', 'Babhai Naka, Borivali West', '11519018000631', TRUE),
                                                                                            (3, 'Sai Lee Dosa Center', 'Suresh Shetty', 'Near Moksh Plaza, SV Road, Borivali West', '11520021000845', TRUE),
                                                                                            (4, 'Radha Krishna Dabeli', 'Kanti G. Bhanushali', 'Near Borivali Station Platform 1, Borivali West', '11522019000198', TRUE),
                                                                                            (5, 'Surat Mix Sev Khamani & Locho', 'Pravin R. Joshi', 'Chandavarkar Road, Borivali West', '11521014000720', TRUE),
                                                                                            (6, 'Balaji Fast Food & Frankie', 'Ramesh N. Solanki', 'Thakur Complex, Borivali East', '11520017000553', TRUE),
                                                                                            (7, 'Pappu Chat Bhandar (Pani Puri)', 'Dinesh Gupta', 'Devidas Road, Near LIC Colony, Borivali West', '11518016000389', TRUE),
                                                                                            (8, 'Babu Vada Pav & Misal', 'Tukaram K. Jadhav', 'Near Chamunda Circle, Borivali West', '11519012000214', TRUE),
                                                                                            (9, 'Super Jalebi & Fafda Center', 'Govindbhai Shah', 'Kasturba Cross Road No. 2, Borivali East', '11523018000965', TRUE),
                                                                                            (10, 'Gourmet Momos & Chinese Stall', 'Kisan Lama', 'Near Veer Savarkar Udyan, Borivali West', '11522020000481', TRUE),
                                                                                            (11, 'Patil Vada Pav', 'Sanjay Patil', 'Near Holy Cross Church, IC Colony, Borivali West', '11521015000911', TRUE),
                                                                                            (12, 'Pipasa Lassi & Fast Food', 'Kailash Sharma', 'Opp. Borivali Railway Station (West), Borivali West', '11519018000922', TRUE),
                                                                                            (13, 'Mangesh Vada Pav', 'Mangesh Parab', 'Near Babhai Naka Signal, Borivali West', '11520021000933', TRUE),
                                                                                            (14, 'Baba Kulfi & Falooda', 'Harish Chawla', 'Ambawadi, SV Road, Borivali East', '11522019000944', TRUE),
                                                                                            (15, 'IC Colony Frankie & Rolls', 'Anthony D Souza', 'IC Colony Cross Road No. 4, Borivali West', '11521014000955', TRUE)
    ON DUPLICATE KEY UPDATE
                         stall_name = VALUES(stall_name),
                         owner_name = VALUES(owner_name),
                         location = VALUES(location),
                         fssai_number = VALUES(fssai_number),
                         fssai_verified = VALUES(fssai_verified);

INSERT INTO review (id, vendor_id, customer_name, rating, comment_text, created_at) VALUES
                                                                                        (1, 1, 'Amit Sharma', 5, 'Best cheese grilled sandwich in Borivali! Clean prep area and fresh ingredients.', NOW()),
                                                                                        (2, 2, 'Pooja Gala', 4, 'Crispy vada and quick service at Babhai Naka. Clean and well maintained.', NOW()),
                                                                                        (3, 3, 'Rohan Shetty', 5, 'Crispy Mysore Masala Dosa, authentic taste and quick hygiene check passed.', NOW()),
                                                                                        (4, 4, 'Bhavin Bhanushali', 4, 'Authentic Kutch style spicy dabeli near platform 1. Fast service.', NOW()),
                                                                                        (5, 5, 'Jignesh Joshi', 5, 'Fresh, soft nylon khamani and piping hot locho. Very neat stall.', NOW()),
                                                                                        (6, 6, 'Sneha Tiwari', 4, 'Great paneer cheese frankie in Thakur Complex. Clean gloves used.', NOW()),
                                                                                        (7, 7, 'Karan Trivedi', 5, 'Pani puri water made with mineral water! Extremely safe and tasty.', NOW()),
                                                                                        (8, 8, 'Nitin Jadhav', 4, 'Classic Mumbai vada pav with spicy red chutney near Chamunda Circle.', NOW()),
                                                                                        (9, 9, 'Hiren Shah', 5, 'Crispy fafda and sweet jalebi served fresh every morning.', NOW()),
                                                                                        (10, 10, 'Tenzin Norbu', 4, 'Steamed momos with fiery schezwan sauce. Clean counter.', NOW()),
                                                                                        (11, 11, 'Derick D Costa', 5, 'Legendary Patil Vada Pav in IC Colony! Hot, spicy, and always fresh.', NOW()),
                                                                                        (12, 12, 'Vikas Patel', 5, 'Thick chilled mango lassi right outside Borivali station. Lifesaver in afternoons.', NOW()),
                                                                                        (13, 13, 'Siddhesh Parab', 5, 'The iconic Mangesh Vada Pav at Babhai. The chutney blend is unmatched.', NOW()),
                                                                                        (14, 14, 'Manish Chawla', 5, 'Creamy Malai and Kesar Pista kulfi at Ambawadi. Top notch quality.', NOW()),
                                                                                        (15, 15, 'Karen Fernandes', 4, 'Best late night frankie spot inside IC Colony. Neat and fast preparation.', NOW())
    ON DUPLICATE KEY UPDATE
                         customer_name = VALUES(customer_name),
                         rating = VALUES(rating),
                         comment_text = VALUES(comment_text),
                         created_at = VALUES(created_at);